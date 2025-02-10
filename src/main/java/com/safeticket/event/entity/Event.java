package com.safeticket.event.entity;

import com.safeticket.common.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "EVENT_ID")
    private Long id;

    private String name;
    private String date_time;
    private String location;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Builder
    public Event(String name, String date_time, String location, EventStatus status) {
        this.name = name;
        this.date_time = date_time;
        this.location = location;
        this.status = status;
    }

    @Builder
    public Event(Long id, String name, String date_time, String location, EventStatus status, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.name = name;
        this.date_time = date_time;
        this.location = location;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
