package com.safeticket.order.repository;

import com.safeticket.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN o.orderTickets ot WHERE o.userId = :userId AND ot.ticketId IN :ticketIds ORDER BY o.id DESC")
    List<Order> findByUserIdAndTicketIds(@Param("userId") Long userId, @Param("ticketIds") List<Long> ticketIds, Pageable pageable);
}
