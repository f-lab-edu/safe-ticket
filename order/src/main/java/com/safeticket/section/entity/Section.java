package com.safeticket.section.entity;

import com.safeticket.common.util.BaseTimeEntity;
import com.safeticket.seat.entity.Seat;
import com.safeticket.venue.entity.Venue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Section extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sectionId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venueId", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private String name;

    private Integer capacity;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();
}
