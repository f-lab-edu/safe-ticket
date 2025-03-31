package com.safeticket.event.dto;

import com.safeticket.event.entity.Event;
import com.safeticket.showtime.dto.ShowtimeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String name;
    private String description;
    private Integer durationMinutes;
    private String status;
    private List<ShowtimeDTO> showtimes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventDTO of(Event event, boolean includeShowtimes) {
        if (event == null) {
            return null;
        }
        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .durationMinutes(event.getDurationMinutes())
                .status(event.getStatus().getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .showtimes(includeShowtimes ? ShowtimeDTO.ofList(event.getShowtimes()) : null)  // 상세 조회 시만 포함
                .build();
    }

    public static List<EventDTO> ofList(List<Event> events) {
        return events.stream().map(event -> EventDTO.of(event, false)).toList();
    }
}
