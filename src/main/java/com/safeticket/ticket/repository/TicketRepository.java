package com.safeticket.ticket.repository;

import com.safeticket.ticket.domain.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t.id FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.status = 'AVAILABLE'")
    List<Long> findAvailableTickets(@Param("showtimeId") Long showtimeId);

    @Query(value = "SELECT * FROM ticket WHERE ticket_id IN (:ticketIds) AND status = 'AVAILABLE' FOR UPDATE NOWAIT", nativeQuery = true)
    List<Ticket> findAvailableTicketsWithLock(@Param("ticketIds") List<Long> ticketIds);
}
