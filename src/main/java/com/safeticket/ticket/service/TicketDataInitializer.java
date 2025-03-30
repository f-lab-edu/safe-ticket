package com.safeticket.ticket.service;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.ticket.domain.TicketStatus;
import com.safeticket.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketDataInitializer {

    private final TicketRepository ticketRepository;
    private final RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        Long showtimeId = 1L;
        List<Long> tickets = ticketRepository.findAvailableTickets(showtimeId);
        cacheTicketInRedis(tickets);
    }

    /**
     * Redis에 티켓을 캐시합니다.
     * @param ticketIds 초기화할 티켓 ID 리스트
     */
    public void cacheTicketInRedis(List<Long> ticketIds) {
        RBatch batch = redissonClient.createBatch();
        for (Long ticketId : ticketIds) {
            String redisKey = RedisKeyUtil.getTicketKey(String.valueOf(ticketId));
            batch.getBucket(redisKey).setAsync(TicketStatus.AVAILABLE.name());
        }
        batch.execute();
    }
}