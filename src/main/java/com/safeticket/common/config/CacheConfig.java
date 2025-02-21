package com.safeticket.common.config;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URISyntaxException;
import java.util.Objects;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public JCacheCacheManager cacheManager() throws URISyntaxException {
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        CacheManager cacheManager = provider.getCacheManager(
                Objects.requireNonNull(getClass().getResource("/ehcache.xml")).toURI(),
                provider.getDefaultClassLoader());
        return new JCacheCacheManager(cacheManager);
    }
}