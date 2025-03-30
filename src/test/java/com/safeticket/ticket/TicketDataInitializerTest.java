package com.safeticket.ticket;

import com.safeticket.common.leader.LeaderElectionService;
import com.safeticket.ticket.repository.TicketRepository;
import com.safeticket.ticket.service.TicketDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketDataInitializerTest {

    @Mock
    private RedissonClient redissonClient;

    @InjectMocks
    private TicketDataInitializer ticketDataInitializer;

    @Mock
    private RBatch rBatch;

    @BeforeEach
    void setUp() {
        when(redissonClient.createBatch()).thenReturn(rBatch);
        when(rBatch.getBucket(anyString())).thenReturn(mock(RBucket.class));
    }

    @Test
    public void cacheTicketInRedisShouldCacheTickets() {
        // given
        List<Long> ticketIds = Arrays.asList(1L, 2L, 3L);

        // when
        ticketDataInitializer.cacheTicketInRedis(ticketIds);

        // then
        for (Long ticketId : ticketIds) {
            verify(rBatch.getBucket("ticket:" + ticketId), times(ticketIds.size())).setAsync("AVAILABLE");
        }
        verify(rBatch, times(1)).execute();
    }

}