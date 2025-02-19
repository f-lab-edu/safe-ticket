package com.safeticket.showtime.service;

import com.safeticket.showtime.entity.Showtime;

import java.util.List;

public interface ShowtimeService {
    Showtime getShowtime(Long showtimeId);
    List<Showtime> getAllShowtimes();
}
