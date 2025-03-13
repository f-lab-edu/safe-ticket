package com.safeticket.ticket.service;

import com.safeticket.ticket.domain.Ticket;
import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import com.safeticket.ticket.repository.TicketRepository;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Value("${ticket.expiration.minutes}")
    private int expirationMinutes;

    @Override
    @Cacheable("ehcache_availableTickets")
    public AvailableTicketsDTO getAvailableTickets(Long showtimeId) {
        List<Long> ticketIds = ticketRepository.findAvailableTickets(showtimeId);
        return AvailableTicketsDTO.builder()
                .ticketIds(ticketIds)
                .build();
    }

    @Override
    @Cacheable(cacheNames = "redis_availableTickets", key = "#showtimeId", cacheResolver = "cacheResolver")
    public AvailableTicketsDTO getAvailableTickets_redis(Long showtimeId) {
        List<Long> ticketIds = ticketRepository.findAvailableTickets(showtimeId);
        return AvailableTicketsDTO.builder()
                .ticketIds(ticketIds)
                .build();
    }

    @Override
    @Transactional
    public void reserveTickets(TicketDTO ticketDTO) {
        try {
            List<Ticket> tickets = ticketRepository.findAvailableTicketsWithLock(ticketDTO.getTicketIds());
            validateTickets(tickets, ticketDTO.getTicketIds());

            Duration expirationTime = Duration.ofMinutes(expirationMinutes);

            for(Ticket ticket : tickets) {
                ticket.reserve(ticketDTO.getUserId(), expirationTime);
            }

            ticketRepository.saveAll(tickets);
        } catch (PessimisticLockException | ConcurrencyFailureException e) {
            throw new TicketsNotAvailableException();
        }
    }

    private void validateTickets(List<Ticket> tickets, List<Long> ticketIds) {
        if(tickets.size() != ticketIds.size()) {
            throw new TicketsNotAvailableException();
        }
    }
}
