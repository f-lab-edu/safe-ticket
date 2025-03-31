package com.safeticket.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long userId;
    private Long showtimeId;
    private List<Long> ticketIds;
    private Map<Long, String> ticketStatuses;
}
