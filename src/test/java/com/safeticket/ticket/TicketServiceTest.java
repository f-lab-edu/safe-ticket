package com.safeticket.ticket;

import com.safeticket.ticket.domain.Ticket;
import com.safeticket.ticket.domain.TicketStatus;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import com.safeticket.ticket.service.TicketService;
import com.safeticket.ticket.repository.TicketRepository;
import com.safeticket.ticket.service.TicketServiceImpl;
import jakarta.persistence.PessimisticLockException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    Ticket ticket1;
    Ticket ticket2;
    TicketDTO ticketDTO;

    @BeforeEach
    void setUp() {
        ticket1 = Ticket.builder()
                .id(1L)
                .userId(1L)
                .price(10000)
                .status(TicketStatus.AVAILABLE)
                .build();

        ticket2 = Ticket.builder()
                .id(2L)
                .userId(1L)
                .price(10000)
                .status(TicketStatus.AVAILABLE)
                .build();

        ticketDTO = TicketDTO.builder()
                .userId(1L)
                .ticketIds(Arrays.asList(1L, 2L))
                .build();
    }

    @Test
    public void getReserveTickets_shouldReserveAndSaveTickets(){
        // given
        when(ticketRepository.findAvailableTicketsWithLock(ticketDTO.getTicketIds()))
                .thenReturn(Arrays.asList(ticket1, ticket2));

        // when
        ticketService.reserveTickets(ticketDTO);

        // then
        assertThat(ticket1.getStatus()).isEqualTo(TicketStatus.RESERVED);
        assertThat(ticket2.getStatus()).isEqualTo(TicketStatus.RESERVED);
    }

    @Test
    public void getReserveTickets_shouldThrowException_whenTicketNotAvailable(){
        // given
        when(ticketRepository.findAvailableTicketsWithLock(ticketDTO.getTicketIds()))
                .thenReturn(Collections.singletonList(ticket1));

        // when & then
        assertThatThrownBy(() -> ticketService.reserveTickets(ticketDTO))
                .isInstanceOf(TicketsNotAvailableException.class);
    }

    @Test
    public void getReserveTickets_shouldThrowException_whenLockIsAlreadyAcquired() {
        when(ticketRepository.findAvailableTicketsWithLock(anyList())).thenThrow(new PessimisticLockException());

        Assertions.assertThatThrownBy(() -> ticketService.reserveTickets(ticketDTO))
                .isInstanceOf(TicketsNotAvailableException.class);
    }
}
