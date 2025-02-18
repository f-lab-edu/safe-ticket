package com.safeticket.showtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.safeticket.event.entity.Event;
import com.safeticket.showtime.entity.Showtime;
import com.safeticket.venue.entity.Venue;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShowtimeResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Event event;
    private Venue venue;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShowtimeResponse of(Showtime showtime) {
        if (showtime == null) {
            return null;
        }
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .event(showtime.getEvent())
                .venue(showtime.getVenue())
                .createdAt(showtime.getCreatedAt())
                .updatedAt(showtime.getUpdatedAt())
                .build();
    }

    public static List<ShowtimeResponse> ofList(List<Showtime> showtimes) {
        return (showtimes == null) ? Collections.emptyList()
                : showtimes.stream().map(ShowtimeResponse::of).collect(Collectors.toList());
    }


}
