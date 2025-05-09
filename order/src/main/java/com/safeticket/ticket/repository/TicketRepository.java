package com.safeticket.ticket.repository;

import com.safeticket.ticket.entity.Ticket;
import com.safeticket.ticket.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("UPDATE Ticket t SET t.status = :status WHERE t.id = :ticketId")
    void updateTicketStatus(@Param("ticketId") Long ticketId, @Param("status") TicketStatus status);
}
