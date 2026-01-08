package com.example.sixpark.domain.showinfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheEvictionService {

    private final CacheManager cacheManager;

    /**
     * 검색 캐시 전체 삭제
     */
    @CacheEvict(value = "showInfoSearch", allEntries = true)
    public void evictSearchCache() {}

    /**
     * 모든 캐시 삭제
     */
    public void evictAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }
}
