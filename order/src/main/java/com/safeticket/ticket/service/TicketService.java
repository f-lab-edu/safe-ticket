package com.safeticket.ticket.service;

import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.entity.TicketStatus;


public interface TicketService {

    AvailableTicketsDTO getTickets(Long showtimeId);
    TicketDTO reserveTickets(TicketDTO ticketDTO);
    void handleExpiredKey(String expiredKey);
    void updateTicketStatus(Long ticketId, TicketStatus status);
}
