package com.safeticket.event.entity;

import com.safeticket.common.util.BaseTimeEntity;
import com.safeticket.showtime.entity.Showtime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventId")
    private Long id;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '이벤트명'")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '이벤트 설명'")
    private String description;

    @Column(columnDefinition = "INT COMMENT '공연시간'")
    private Integer durationMinutes;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Showtime> showtimes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Builder
    public Event(Long id, String name, String description, Integer durationMinutes, List<Showtime> showtimes, EventStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.showtimes = showtimes;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
