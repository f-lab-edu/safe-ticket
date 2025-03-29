package com.safeticket.common.cache;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class CustomCacheResolver {

    private final CacheManager ehcacheCacheManager;
    private final CacheManager redisCacheManager;

    @Autowired
    public CustomCacheResolver(@Qualifier("ehcacheCacheManager") CacheManager ehcacheCacheManager,
                               @Qualifier("redisCacheManager") CacheManager redisCacheManager) {
        this.ehcacheCacheManager = ehcacheCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    @Bean
    public CacheResolver cacheResolver() {
        return new CacheResolver() {
            @Override
            @NonNull
            public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
                String cacheName = context.getOperation().getCacheNames().iterator().next();
                CacheManager cacheManager = getCacheManager(cacheName);
                Cache cache = cacheManager.getCache(cacheName);
                return cache != null ? Collections.singleton(cache) : Collections.emptyList();
            }

            private CacheManager getCacheManager(String cacheName) {
                if (cacheName.startsWith("redis")) {
                    return redisCacheManager;
                } else {
                    return ehcacheCacheManager;
                }
            }
        };
    }
}
