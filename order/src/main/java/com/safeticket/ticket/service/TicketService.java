package com.safeticket.ticket.service;

import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;


public interface TicketService {

    AvailableTicketsDTO getTickets(Long showtimeId);
    TicketDTO reserveTickets(TicketDTO ticketDTO);
    void handleExpiredKey(String expiredKey);
}
