package com.safeticket.event;

import com.safeticket.event.dto.EventResponse;
import com.safeticket.event.entity.Event;
import com.safeticket.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(EventResponse.of(eventService.getEventById(id)));
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<EventResponse> getEventByIdWithShowtimes(@PathVariable Long id) {
        return ResponseEntity.ok(EventResponse.of(eventService.getEventByIdWithShowtimes(id)));
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }
}
