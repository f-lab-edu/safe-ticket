package com.safeticket.ticket;

import com.safeticket.common.advice.GlobalExceptionHandler;
import com.safeticket.ticket.controller.TicketController;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import com.safeticket.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void reserveTicketsShouldReturnStatusOKWhenSuccessful() throws Exception {
        // when & then
        mockMvc.perform(put("/tickets/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"ticketIds\":[1,2]}"))
                .andExpect(status().isOk());
    }

    @Test
    public void reserveTicketsShouldReturnStatusConflictWhenTicketsNotAvailable() throws Exception {
        // given
        doThrow(new TicketsNotAvailableException()).when(ticketService).reserveTickets(any(TicketDTO.class));

        // when & then
        mockMvc.perform(put("/tickets/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"ticketIds\":[1,2]}"))
                .andExpect(status().isOk());
    }
}
