package com.safeticket.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(trackMetrics)")
    public Object incrementCounter(ProceedingJoinPoint joinPoint, TrackMetrics trackMetrics) throws Throwable {
        Counter counter = meterRegistry.counter(trackMetrics.value());
        counter.increment();
        return joinPoint.proceed();
    }
}