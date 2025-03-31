package com.safeticket.common.leader;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.ticket.service.TicketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {

    private final TicketServiceImpl ticketService;
    private final LeaderElectionService leaderElectionService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (leaderElectionService.isLeader()) {
            String expiredKey = message.toString();
            if(expiredKey.contains(RedisKeyUtil.getLockTicketKey(RedisKeyUtil.EMPTY_STRING))) {
                ticketService.handleExpiredKey(expiredKey);
            }
        }
    }
}
