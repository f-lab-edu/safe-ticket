package com.safeticket.venue.entity;

import com.safeticket.common.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venueId")
    private Long id;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '공연장명'")
    private String name;

    @Enumerated(EnumType.STRING)
    private Address address;

    @Column(columnDefinition = "INT UNSIGNED COMMENT '총 좌석 수'")
    private int capacity;

    @Builder
    public Venue(String name, Address address, int capacity) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }
}
