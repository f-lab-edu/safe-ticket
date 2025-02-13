package com.safeticket.event.service;

import com.safeticket.event.entity.Event;
import com.safeticket.event.EventRepository;
import com.safeticket.event.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventByIdWithShowtimes(Long id) {
        return eventRepository.findByIdWithShowtimes(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }
}
