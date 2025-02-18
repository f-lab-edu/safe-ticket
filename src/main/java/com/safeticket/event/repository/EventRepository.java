package com.safeticket.event.repository;

import com.safeticket.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN FETCH e.showtimes WHERE e.id = :id")
    Optional<Event> findByIdWithShowtimes(Long id);
}
