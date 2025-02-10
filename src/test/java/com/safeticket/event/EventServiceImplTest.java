package com.safeticket.event;

import com.safeticket.event.dto.EventResponse;
import com.safeticket.event.entity.Event;
import com.safeticket.event.entity.EventStatus;
import com.safeticket.event.service.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    Event event;

    EventResponse eventResponse;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .name("이벤트명")
                .date_time("2023-10-10T10:00:00")
                .location("장소")
                .status(EventStatus.PUBLISHED)
                .build();
    }

    @Test
    public void getEventById_shouldReturnEvent_whenEventExist() {
        // given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // when
        Event result = eventService.getEventById(1L);

        // then
        assertThat(result).isEqualTo(event);
    }
}
