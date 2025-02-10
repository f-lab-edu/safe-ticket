package com.safeticket.event.service;

import com.safeticket.event.entity.Event;

import java.util.List;

public interface EventService {
    Event getEventById(Long id);
    List<Event> getAllEvents();
}
