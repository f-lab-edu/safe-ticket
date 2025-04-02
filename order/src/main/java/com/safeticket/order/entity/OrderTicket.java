package com.safeticket.order.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orderTickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderTicketId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    @JsonBackReference
    private Order order;

    @Column(name = "ticketId", nullable = false)
    private Long ticketId;

    @Builder
    public OrderTicket(Order order, Long ticketId) {
        this.order = order;
        this.ticketId = ticketId;
    }
}
