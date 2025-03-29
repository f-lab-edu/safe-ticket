package com.safeticket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SafeTicketApplicationTests {

    @Mock
    private RedissonClient redissonClient;

    @InjectMocks
    private SafeTicketApplication safeTicketApplication;

    @Test
    void contextLoads() {
    }

}
