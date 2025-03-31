package com.safeticket.event.dto;

import com.safeticket.event.entity.Event;
import com.safeticket.showtime.dto.ShowtimeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    private List<ShowtimeResponse> showtimes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventResponse of(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .durationMinutes(event.getDurationMinutes())
                .status(event.getStatus().getStatus())
                .showtimes(event.getShowtimes() == null ? null : ShowtimeResponse.ofList(event.getShowtimes()))
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
