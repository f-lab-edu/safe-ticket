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

    @TrackMetrics("showtime_tickets_requests_total")
    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<AvailableTicketsDTO> getShowtimeTickets(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(ticketService.getTickets(showtimeId));
    }

    @TrackMetrics("reservation_requests_total")
    @PutMapping("/reservations")
    public ResponseEntity<TicketDTO> reserveTickets(@RequestBody TicketDTO ticketDTO) {
        return ResponseEntity.ok(ticketService.reserveTickets(ticketDTO));
    }
}
