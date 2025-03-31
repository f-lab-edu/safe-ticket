package com.safeticket.common.leader;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LeaderElectionServiceTest {

    private RLock lock;
    private LeaderElectionService leaderElectionService;
    RedissonClient redissonClient;

    @BeforeEach
    void setUp() {
        redissonClient = mock(RedissonClient.class);
        lock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        leaderElectionService = new LeaderElectionService(redissonClient);

        ReflectionTestUtils.setField(leaderElectionService, "LEADER_TTL_SECONDS", 5L);
        ReflectionTestUtils.setField(leaderElectionService, "waitTimeSeconds", 0L);
    }

    @Test
    void testTryToBecomeLeader() throws InterruptedException {
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        Thread leaderElectionThread = new Thread(() -> leaderElectionService.electLeader());
        leaderElectionThread.start();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(leaderElectionService::isLeader);


        assertTrue(leaderElectionService::isLeader);
        verify(lock, atLeastOnce()).tryLock(anyLong(), anyLong(), any(TimeUnit.class));

        // 스레드 종료를 위해 인터럽트
        leaderElectionThread.interrupt();
        leaderElectionThread.join();
    }
}