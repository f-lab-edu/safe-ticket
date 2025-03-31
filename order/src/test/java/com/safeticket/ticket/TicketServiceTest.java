package com.safeticket.ticket;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.ticket.entity.TicketStatus;
import com.safeticket.ticket.dto.AvailableTicketsDTO;
import com.safeticket.ticket.dto.TicketDTO;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import com.safeticket.ticket.repository.TicketRepository;
import com.safeticket.ticket.service.TicketServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RBatch rBatch;

    @Mock
    private RLock rLock;

    @Spy
    @InjectMocks
    private TicketServiceImpl ticketService;

    TicketDTO ticketDTO;

    @BeforeEach
    void setUp() {
        lenient().when(redissonClient.createBatch()).thenReturn(rBatch);
        lenient().when(rBatch.getBucket(anyString())).thenReturn(mock(RBucket.class));

        ticketDTO = TicketDTO.builder()
                .userId(1L)
                .ticketIds(Arrays.asList(1L, 2L))
                .build();
    }

    @Test
    public void getTicketsShouldReturnAvailableTickets() {
        // given
        Long showtimeId = 1L;
        List<Long> tickets = Arrays.asList(1L, 2L, 3L);
        when(ticketRepository.findAvailableTickets(showtimeId)).thenReturn(tickets);

        // when
        AvailableTicketsDTO result = ticketService.getTickets(showtimeId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTicketIds()).isEqualTo(tickets);
        verify(ticketRepository, times(1)).findAvailableTickets(showtimeId);
    }

    @Test
    public void reserveTicketsShouldReserveAndSaveTickets() throws Exception {
        // given
        RLock lock1 = mock(RLock.class);
        RLock lock2 = mock(RLock.class);
        lenient().when(redisTemplate.hasKey("ticket:1")).thenReturn(true);
        lenient().when(redisTemplate.hasKey("ticket:2")).thenReturn(true);
        lenient().when(redissonClient.getLock("lock:ticket:1")).thenReturn(lock1);
        lenient().when(redissonClient.getLock("lock:ticket:2")).thenReturn(lock2);
        lenient().when(lock1.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        lenient().when(lock2.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // RedissonMultiLock의 Mock 객체를 사용하도록 수정
        RedissonMultiLock multiLockSpy = spy(new RedissonMultiLock(lock1, lock2));
        doReturn(true).when(multiLockSpy).tryLock(anyLong(), anyLong(), any(TimeUnit.class));

        // ticketService의 createMultiLock 메서드가 multiLockSpy를 반환하도록 수정
        doReturn(multiLockSpy).when(ticketService).createMultiLock(anyList());

        // redisTemplate.opsForValue() 모킹
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), any(Duration.class));

        // when
        ticketService.reserveTickets(ticketDTO);

        // then
        verify(valueOperations, times(2)).set(anyString(), eq(TicketStatus.RESERVED.name()), any(Duration.class));
    }

    @Test
    public void reserveTicketsShouldThrowExceptionWhenLockIsAlreadyAcquired() throws InterruptedException {
        // given
        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        // when & then
        Assertions.assertThatThrownBy(() -> ticketService.reserveTickets(ticketDTO))
                .isInstanceOf(TicketsNotAvailableException.class);
    }

    @Test
    public void reserveTicketsShouldThrowExceptionWhenTicketNotAvailable() {
        // given
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // when & then
        Assertions.assertThatThrownBy(() -> ticketService.reserveTickets(ticketDTO))
                .isInstanceOf(TicketsNotAvailableException.class);
    }

    @Test
    public void updateTicketStatusToReservedShouldUpdateStatus() throws Exception {
        // given
        when(redissonClient.createBatch()).thenReturn(rBatch);

        // when
        Method method = TicketServiceImpl.class.getDeclaredMethod("updateTicketStatusToReserved", TicketDTO.class);
        method.setAccessible(true);
        method.invoke(ticketService, ticketDTO);

        // then
        verify(ticketRepository, times(1)).updateTicketStatus(1L, TicketStatus.RESERVED);
        verify(ticketRepository, times(1)).updateTicketStatus(2L, TicketStatus.RESERVED);
        verify(rBatch, times(1)).execute();
    }

    @Test
    public void handleExpiredKeyShouldUpdateTicketStatusToAvailable(){
        // given
        String expiredKey = RedisKeyUtil.getLockTicketKey("1");
        Long ticketId = 1L;

        // when
        ticketService.handleExpiredKey(expiredKey);

        // then
        verify(ticketRepository, times(1)).updateTicketStatus(ticketId, TicketStatus.AVAILABLE);
    }
}
