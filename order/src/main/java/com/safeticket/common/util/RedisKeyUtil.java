package com.safeticket.common.util;

public class RedisKeyUtil {
    private static final String TICKET_KEY_PREFIX = "ticket:";
    private static final String LOCK_KEY_PREFIX = "lock:";
    public static final String LEADER_KEY = "leader-election";
    public static final String EMPTY_STRING = "";

    public static String getTicketKey(String ticketId) {
        return TICKET_KEY_PREFIX + ticketId;
    }

    public static String getLockTicketKey(String ticketId) {
        return LOCK_KEY_PREFIX + TICKET_KEY_PREFIX + ticketId;
    }

    public static String getReservationLockKey(Long userId, Long ticketId) {
        return "reservation:" + userId + ":" + ticketId;
    }
}
