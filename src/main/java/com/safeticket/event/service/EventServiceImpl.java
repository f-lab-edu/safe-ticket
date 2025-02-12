package com.safeticket.event.service;

import com.safeticket.event.entity.Event;
import com.safeticket.event.EventRepository;
import com.safeticket.event.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
