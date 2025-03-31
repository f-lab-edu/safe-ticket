package com.safeticket.seat.entity;

import com.safeticket.common.util.BaseTimeEntity;
import com.safeticket.section.entity.Section;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seatId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId", nullable = false)
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(name = "seatType")
    private SeatType type;

    @Column(columnDefinition = "VARCHAR(10) COMMENT '행'")
    private String seatRow;

    @Column(columnDefinition = "VARCHAR(10) COMMENT '좌석번호'")
    private String seatNumber;
}
