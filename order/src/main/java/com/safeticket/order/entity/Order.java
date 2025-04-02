package com.safeticket.order.entity;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.safeticket.common.util.BaseTimeEntity;
import com.safeticket.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Long id;

    private Long userId;

    private Integer amount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderTicket> orderTickets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Builder
    public Order(Long id, Long userId, Integer amount, List<OrderTicket> orderTickets, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.orderTickets = orderTickets != null ? orderTickets : new ArrayList<>();
        this.status = status;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
