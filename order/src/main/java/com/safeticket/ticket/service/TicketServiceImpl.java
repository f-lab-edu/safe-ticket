package com.safeticket.ticket.service;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.ticket.entity.TicketStatus;
import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import com.safeticket.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    @Value("${redis.lock.ticket.reservation.no-wait-time-seconds}")
    private long waitTimeSeconds;

    @Value("${redis.lock.ticket.reservation.lease-time-seconds}")
    private long leaseTimeSeconds;

    @Override
    @Cacheable(cacheNames = "redis_showtimeTickets", key = "#showtimeId", cacheResolver = "cacheResolver")
    public AvailableTicketsDTO getTickets(Long showtimeId) {
        List<Long> tickets = ticketRepository.findAvailableTickets(showtimeId);
        return AvailableTicketsDTO.builder()
                .ticketIds(tickets)
                .build();
    }

    @Override
    @Transactional
    public TicketDTO reserveTickets(TicketDTO ticketDTO) {
        List<RLock> locks = getLocksForTickets(ticketDTO.getTicketIds());
        RedissonMultiLock multiLock = createMultiLock(locks);

        try {
            boolean lockAcquired = tryLock(multiLock);
            if (!lockAcquired) {
                throw new TicketsNotAvailableException();
            }

            reserveTicketsInRedis(ticketDTO);
            updateTicketStatusToReserved(ticketDTO);
            invalidateCache(ticketDTO.getShowtimeId());

            return ticketDTO;
        } catch (InterruptedException e) {
            multiLock.unlock();
            throw new TicketsNotAvailableException();
        }
    }

    private List<RLock> getLocksForTickets(List<Long> ticketIds) {
        List<RLock> locks = new ArrayList<>();
        for (Long ticketId : ticketIds) {
            String redisKey = RedisKeyUtil.getTicketKey(String.valueOf(ticketId));

            if (!redisTemplate.hasKey(redisKey)) {
                throw new TicketsNotAvailableException();
            }

            String lockKey = RedisKeyUtil.getLockTicketKey(String.valueOf(ticketId));
            RLock lock = redissonClient.getLock(lockKey);
            locks.add(lock);
        }
        return locks;
    }

    public RedissonMultiLock createMultiLock(List<RLock> locks) {
        return new RedissonMultiLock(locks.toArray(new RLock[0]));
    }

    private boolean tryLock(RedissonMultiLock multiLock) throws InterruptedException {
        return multiLock.tryLock(waitTimeSeconds, leaseTimeSeconds, TimeUnit.SECONDS);
    }

    private void reserveTicketsInRedis(TicketDTO ticketDTO) {
        for (Long ticketId : ticketDTO.getTicketIds()) {
            String reservationKey = RedisKeyUtil.getReservationLockKey(ticketDTO.getUserId(), ticketId);
            redisTemplate.opsForValue().set(reservationKey, TicketStatus.RESERVED.name(), Duration.ofSeconds(leaseTimeSeconds));
        }
    }

    private void updateTicketStatusToReserved(TicketDTO ticketDTO) {
        for(Long ticketId : ticketDTO.getTicketIds()) {
            ticketRepository.updateTicketStatus(ticketId, TicketStatus.RESERVED);
        }

        RBatch batch = redissonClient.createBatch();
        for (Long ticketId : ticketDTO.getTicketIds()) {
            String redisKey = RedisKeyUtil.getTicketKey(String.valueOf(ticketId));
            batch.getBucket(redisKey).setAsync(TicketStatus.RESERVED.name());
        }
        batch.execute();
    }

    private void invalidateCache(Long showtimeId) {
        String cacheKey = "redis_showtimeTickets::" + showtimeId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 만료된 티켓의 상태를 초기화하는 메서드
     * @param expiredKey 만료된 티켓의 Redis Key
     */
    @Override
    @Transactional
    public void handleExpiredKey(String expiredKey) {
        if(expiredKey.startsWith(RedisKeyUtil.getLockTicketKey(""))) {
            Long ticketId = Long.valueOf(expiredKey.split(":")[2]);
            ticketRepository.updateTicketStatus(ticketId, TicketStatus.AVAILABLE);
        }
    }

    @Override
    @Transactional
    public void updateTicketStatus(Long ticketId, TicketStatus status) {
        ticketRepository.updateTicketStatus(ticketId, status);
    }
}
