package com.safeticket.event.dto;

import com.safeticket.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private Integer durationMinutes;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventResponse of(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .durationMinutes(event.getDurationMinutes())
                .status(event.getStatus().getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
