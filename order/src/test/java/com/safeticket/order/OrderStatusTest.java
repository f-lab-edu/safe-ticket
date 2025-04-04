package com.safeticket.order;

import com.safeticket.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderStatusTest {

    @Test
    public void getStatusCodeShoudReturnCanTransition() {
        // PENDING 상태에서 PENDING으로 전환 가능
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.PENDING));

        // PENDING 상태에서 PAID로 전환 가능
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.PAID));

        // PENDING 상태에서 CANCELLED로 전환 불가능
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
    }
}
