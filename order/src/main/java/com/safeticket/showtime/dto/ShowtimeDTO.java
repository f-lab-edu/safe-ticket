package com.safeticket.showtime.dto;

import com.safeticket.showtime.entity.Showtime;
import com.safeticket.venue.dto.VenueDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long eventId;
    private String venueName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShowtimeDTO of(Showtime showtime) {
        if (showtime == null) {
            return null;
        }
        return ShowtimeDTO.builder()
                .id(showtime.getId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .eventId(showtime.getEvent().getId())
                .venueName(showtime.getVenue().getName())
                .createdAt(showtime.getCreatedAt())
                .updatedAt(showtime.getUpdatedAt())
                .build();
    }

    public static List<ShowtimeDTO> ofList(List<Showtime> showtimes) {
        return (showtimes == null) ? Collections.emptyList()
                : showtimes.stream().map(ShowtimeDTO::of).collect(Collectors.toList());
    }
}
