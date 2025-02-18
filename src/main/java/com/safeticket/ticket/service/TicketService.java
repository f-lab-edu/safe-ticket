package com.safeticket.ticket.service;

import com.safeticket.ticket.domain.Ticket;
import com.safeticket.ticket.dto.TicketDTO;

import java.util.List;

public interface TicketService {

    void reserveTickets(TicketDTO ticketDTO);
}
