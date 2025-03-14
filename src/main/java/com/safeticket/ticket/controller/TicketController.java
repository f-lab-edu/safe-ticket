package com.safeticket.ticket.controller;

import com.safeticket.common.metrics.TrackMetrics;
import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketController {

    private final TicketService ticketService;

    @TrackMetrics("available_tickets_requests_total")
    @GetMapping("/available/{showtimeId}")
    public ResponseEntity<AvailableTicketsDTO> getAvailableTickets(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(ticketService.getAvailableTickets(showtimeId));
    }

    @TrackMetrics("available_tickets_requests_total")
    @GetMapping("/available-redis/{showtimeId}")
    public ResponseEntity<AvailableTicketsDTO> getAvailableTickets_redis(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(ticketService.getAvailableTicketsRedis(showtimeId));
    }

    @TrackMetrics("reservation_requests_total")
    @PutMapping("/reservations")
    public ResponseEntity<Void> reserveTickets(@RequestBody TicketDTO ticketDTO) {
        ticketService.reserveTickets(ticketDTO);
        return ResponseEntity.ok().build();
    }
}
