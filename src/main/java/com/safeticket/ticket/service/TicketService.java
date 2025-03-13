package com.safeticket.ticket.service;

import com.safeticket.ticket.domain.Ticket;
import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;

import java.util.List;

public interface TicketService {

    AvailableTicketsDTO getAvailableTickets(Long showtimeId);
    AvailableTicketsDTO getAvailableTickets_redis(Long showtimeId);
    void reserveTickets(TicketDTO ticketDTO);
}
