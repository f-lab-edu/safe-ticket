package com.safeticket.common.leader;


import com.safeticket.common.util.RedisKeyUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class LeaderElectionService {

    private final RedissonClient redissonClient;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionService.class);

    @Value("${leader.election.ttl.seconds}")
    private long LEADER_TTL_SECONDS;

    @Value("${redis.lock.ticket.reservation.no-wait-time-seconds}")
    private long waitTimeSeconds;

    @PostConstruct
    public void electLeader() {
        new Thread(this::tryToBecomeLeader).start();
    }

    private void tryToBecomeLeader() {
        while (true) {
            RLock lock = redissonClient.getLock(RedisKeyUtil.LEADER_KEY);
            try {
                if (lock.tryLock(waitTimeSeconds, LEADER_TTL_SECONDS, TimeUnit.SECONDS)) {
                    isLeader.set(true);
                    logger.info("become leader");
                    Thread.sleep((LEADER_TTL_SECONDS - 1) * 1000);
                }
            } catch (InterruptedException e) {
                isLeader.set(false);
                Thread.currentThread().interrupt();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    isLeader.set(false);
                }
            }
        }
    }

    public boolean isLeader() {
        return isLeader.get();
    }
}