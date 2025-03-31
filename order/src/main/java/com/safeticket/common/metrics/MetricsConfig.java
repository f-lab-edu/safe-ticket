package com.safeticket.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter availableTicketsCounter(MeterRegistry meterRegistry) {
        return Counter.builder("available_tickets_requests_total")
                .description("Total available tickets requests")
                .register(meterRegistry);
    }

    @Bean
    public Counter reservationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("reservation_requests_total")
                .description("Total reservation requests")
                .register(meterRegistry);
    }
}
