package com.safeticket.ticket.controller;

import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketController {

    final TicketService ticketService;

    @PostMapping("/reservations")
    public ResponseEntity<Void> reserveTickets(@RequestBody TicketDTO ticketDTO) {
        ticketService.reserveTickets(ticketDTO);
        return ResponseEntity.ok().build();
    }
}
