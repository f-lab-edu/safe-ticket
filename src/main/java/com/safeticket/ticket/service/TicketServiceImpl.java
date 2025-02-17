package com.safeticket.ticket.service;

import com.safeticket.ticket.domain.Ticket;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import com.safeticket.ticket.repository.TicketRepository;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void reserveTickets(TicketDTO ticketDTO) {
        try {
            List<Ticket> tickets = ticketRepository.findAvailableTicketsWithLock(ticketDTO.getTicketIds());
            if(tickets.size() != ticketDTO.getTicketIds().size()) {
                throw new TicketsNotAvailableException();
            }

            Duration expirationTime = Duration.ofMinutes(5);

            for(Ticket ticket : tickets) {
                ticket.reserve(ticketDTO.getUserId(), expirationTime);
            }

            ticketRepository.saveAll(tickets);
        } catch (PessimisticLockException | LockAcquisitionException e) {
            throw new TicketsNotAvailableException();
        }
    }
}
