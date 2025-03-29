package com.safeticket.ticket.service;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.ticket.domain.TicketStatus;
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

    @Value("${redis.lock.ticket.reservation.wait-time-seconds}")
    private long waitTimeSeconds;

    @Value("${redis.lock.ticket.reservation.lease-time-seconds}")
    private long leaseTimeSeconds;

    @Override
    @Cacheable(cacheNames = "redis_showtimeTickets", key = "#showtimeId", cacheResolver = "cacheResolver")
    public AvailableTicketsDTO getTickets(Long showtimeId) {
        List<Long> tickets = ticketRepository.findAvailableTickets(showtimeId);
        cacheTicketInRedis(tickets);
        return AvailableTicketsDTO.builder()
                .ticketIds(tickets)
                .build();
    }

    public void cacheTicketInRedis(List<Long> ticketIds) {
        RBatch batch = redissonClient.createBatch();
        for (Long ticketId : ticketIds) {
            String redisKey = RedisKeyUtil.getTicketKey(ticketId);
            batch.getBucket(redisKey).setAsync(TicketStatus.AVAILABLE.name());
        }
        batch.execute();
    }

    @Override
    public void reserveTickets(TicketDTO ticketDTO) {
        List<RLock> locks = getLocksForTickets(ticketDTO.getTicketIds());

        RedissonMultiLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));

        try {
            if (!tryLock(multiLock)) {
                throw new TicketsNotAvailableException();
            }

            reserveTicketsInRedis(ticketDTO);
        } catch (InterruptedException e) {
            multiLock.unlock();
            throw new TicketsNotAvailableException();
        }
    }

    private List<RLock> getLocksForTickets(List<Long> ticketIds) {
        List<RLock> locks = new ArrayList<>();
        for (Long ticketId : ticketIds) {
            String redisKey = RedisKeyUtil.getTicketKey(ticketId);

            if (!redisTemplate.hasKey(redisKey)) {
                throw new TicketsNotAvailableException();
            }

            String lockKey = RedisKeyUtil.getLockTicketKey(ticketId);
            RLock lock = redissonClient.getLock(lockKey);
            locks.add(lock);
        }
        return locks;
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
}
