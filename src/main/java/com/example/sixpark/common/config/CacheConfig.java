package com.example.sixpark.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * v3 : Local cache (caffeine)
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        // 검색 결과 캐시
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("showInfoSearch");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                    // 최대 1000개 항목
                .expireAfterWrite(10, TimeUnit.MINUTES)  // 10분 후 만료
                .recordStats()                        // 통계 기록
        );

        return cacheManager;
    }
}
