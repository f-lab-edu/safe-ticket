package com.safeticket.ticket.domain;

import com.safeticket.common.util.BaseTimeEntity;
import com.safeticket.seat.entity.Seat;
import com.safeticket.showtime.entity.Showtime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtimeId", nullable = false)
    private Showtime showtime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatId", nullable = false, unique = true)
    private Seat seat;

    private Long userId;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = true)
    private LocalDateTime expiredAt;

    @Builder
    public Ticket(Long id, Showtime showtime, Seat seat, Long userId, Integer price, TicketStatus status) {
        this.id = id;
        this.showtime = showtime;
        this.seat = seat;
        this.userId = userId;
        this.price = price;
        this.status = status;
    }

    public void reserve(Long userId, Duration expirationTime) {
        this.userId = userId;
        this.status = TicketStatus.RESERVED;
        this.expiredAt = LocalDateTime.now().plus(expirationTime);
    }

    public void confirm() {
        this.status = TicketStatus.CONFIRMED;
        this.expiredAt = null;
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
        this.userId = null;
        this.expiredAt = null;
    }
}
