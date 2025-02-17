package com.safeticket.event;

import com.safeticket.common.advice.GlobalExceptionHandler;
import com.safeticket.event.controller.EventController;
import com.safeticket.event.entity.Event;
import com.safeticket.event.entity.EventStatus;
import com.safeticket.event.exception.EventNotFoundException;
import com.safeticket.event.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;

    Event event1, event2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        event1 = Event.builder()
                .id(1L)
                .name("이벤트명")
                .description("이벤트 설명")
                .durationMinutes(120)
                .showtimes(new ArrayList<>())
                .status(EventStatus.PUBLISHED)
                .build();

        event2 = Event.builder()
                .id(2L)
                .name("이벤트명2")
                .description("이벤트 설명2")
                .durationMinutes(90)
                .showtimes(new ArrayList<>())
                .status(EventStatus.PUBLISHED)
                .build();
    }

    @Test
    public void getEventById_shouldReturnEvent() throws Exception {
        // given
        when(eventService.getEventByIdWithShowtimes(1L)).thenReturn(event1);

        // when & then
        mockMvc.perform(get("/events/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("이벤트명"))
                .andExpect(jsonPath("$.description").value("이벤트 설명"))
                .andExpect(jsonPath("$.durationMinutes").value(120))
                .andExpect(jsonPath("$.status").value("게시"));
    }

    @Test
    public void getEventById_shouldReturn404_whenEventNotExist() throws Exception {
        // given
        when(eventService.getEventByIdWithShowtimes(1L)).thenThrow(new EventNotFoundException(1L));

        // when & then
        mockMvc.perform(get("/events/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllEvents_shouldReturnAllEvents() throws Exception {
        // given
        when(eventService.getAllEvents()).thenReturn(new ArrayList<>());

        // when & then
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
