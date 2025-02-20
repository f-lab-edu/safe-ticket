package com.safeticket.ticket.controller;

import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketController {

    final TicketService ticketService;

    @GetMapping("/available/{showtimeId}")
    public ResponseEntity<AvailableTicketsDTO> getAvailableTickets(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(ticketService.getAvailableTickets(showtimeId));
    }

    @PutMapping("/reservations")
    public ResponseEntity<Void> reserveTickets(@RequestBody TicketDTO ticketDTO) {
        ticketService.reserveTickets(ticketDTO);
        return ResponseEntity.ok().build();
    }
}
