package com.safeticket.event;

import com.safeticket.event.dto.EventDTO;
import com.safeticket.event.entity.Event;
import com.safeticket.event.entity.EventStatus;
import com.safeticket.event.exception.EventNotFoundException;
import com.safeticket.event.repository.EventRepository;
import com.safeticket.event.service.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    Event event;

    Event event2;

    EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .name("이벤트명")
                .durationMinutes(120)
                .description("설명")
                .status(EventStatus.PUBLISHED)
                .build();

        event2 = Event.builder()
                .name("이벤트명2")
                .durationMinutes(90)
                .description("설명2")
                .status(EventStatus.PUBLISHED)
                .build();
    }

    @Test
    public void getEventByIdShouldReturnEventWhenEventExist() {
        // given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // when
        Event result = eventService.getEventById(1L);

        // then
        assertThat(result).isEqualTo(event);
    }

    @Test
    public void getEventByIdShouldThrowEventNotFoundExceptionWhenEventNotExist() {
        // given
        when(eventRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventService.getEventById(1L))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    public void getAllEventsShouldReturnAllEvents() {
        // given
        List<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event2);
        when(eventRepository.findAll()).thenReturn(events);

        // when
        eventService.getAllEvents();

        // then
        assertThat(eventRepository.findAll()).isEqualTo(events);
    }
}
