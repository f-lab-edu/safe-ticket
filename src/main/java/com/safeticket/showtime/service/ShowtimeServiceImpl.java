package com.safeticket.showtime.service;

import com.safeticket.showtime.entity.Showtime;
import com.safeticket.showtime.exception.ShowtimeNotFoundException;
import com.safeticket.showtime.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    @Override
    public Showtime getShowtime(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ShowtimeNotFoundException(id));
    }

    @Override
    public List<Showtime> getAllShowtimes() {
        return List.of();
    }
}
