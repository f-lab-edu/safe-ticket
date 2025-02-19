package com.safeticket.showtime;

import com.safeticket.event.entity.Event;
import com.safeticket.event.entity.EventStatus;
import com.safeticket.showtime.entity.Showtime;
import com.safeticket.showtime.exception.ShowtimeNotFoundException;
import com.safeticket.showtime.repository.ShowtimeRepository;
import com.safeticket.showtime.service.ShowtimeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceImplTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private ShowtimeServiceImpl showtimeService;

    Showtime showtime;

    Event event;

    @BeforeEach
    void setUp() {
        Event event = Event.builder()
                .id(1L)
                .name("이벤트명")
                .description("이벤트 설명")
                .durationMinutes(120)
                .status(EventStatus.PUBLISHED)
                .build();

        showtime = Showtime.builder()
                .id(1L)
                .event(event)
                .startTime(LocalDateTime.of(2025, 3, 3, 12, 0))
                .endTime(LocalDateTime.of(2025, 3, 3, 14, 0))
                .build();
    }

    @Test
    public void getShowtimeByIdShouldReturnShowtime() {
        // given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.ofNullable(showtime));

        // when
        Showtime result = showtimeService.getShowtime(1L);

        // then
        assertThat(result).isEqualTo(showtime);
    }

    @Test
    public void getShowtimeByIdShouldThrowShowtimeNotFoundException() {
        // given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> showtimeService.getShowtime(1L))
                .isInstanceOf(ShowtimeNotFoundException.class);
    }
}
