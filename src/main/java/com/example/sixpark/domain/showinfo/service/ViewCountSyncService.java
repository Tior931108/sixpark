package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.common.lock.RedisScheduleLock;
import com.example.sixpark.domain.genre.repository.GenreRepository;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountSyncService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ShowInfoRepository showInfoRepository;
    private final GenreRepository genreRepository;

    private static final String DAILY_VIEW_KEY_PREFIX = "views:daily:genre:";
    private static final String SYNC_LOCK_KEY = "sync:lock:view-count";

    /**
     * Redis → DB 동기화 (5분마다)
     * cron: 초 분 시 일 월 요일
     * <p>
     * 분산 락으로 중복 스케줄 실행 방지
     */
    @Scheduled(cron = "0 */5 * * * *")  // 5분마다 실행
    @Transactional
    @RedisScheduleLock(key = SYNC_LOCK_KEY, ttl = 300000)
    public void syncViewCountsToDatabase() {
        log.info("=== 조회수 동기화 시작 ===");

        // 오늘 날짜
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        /**
         * 연극, 뮤지컬, 서양음악(클래식), 한국음악(국악), 대중응악, 무용(서양/한국무용), 대중무용, 서커스/마술, 복합
         * >> KOPIS API 기준 장르 9개
         */
        // 모든 장르에 대해 동기화 (1~9번 장르)
        List<Long> genreIds = genreRepository.findAllGenreIds();
        for (Long genreId : genreIds) {
            syncGenreDailyViews(genreId, today);
        }

        log.info("=== 조회수 동기화 완료 ===");
    }

    /**
     * 특정 장르의 일간 조회수 동기화
     * <p>
     * 주간은 redis DB 에서만 저장함.
     */
    private void syncGenreDailyViews(Long genreId, String date) {
        String key = DAILY_VIEW_KEY_PREFIX + genreId + ":" + date;

        // Redis ZSet에서 모든 데이터 조회
        Set<ZSetOperations.TypedTuple<Object>> allViews = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

        if (allViews == null || allViews.isEmpty()) {
            return;
        }

        log.info("장르 {} 동기화 시작: {} 건", genreId, allViews.size());

        int syncCount = 0;
        for (ZSetOperations.TypedTuple<Object> tuple : allViews) {
            try {
                Long showInfoId = Long.parseLong(tuple.getValue().toString());
                Long viewCount = tuple.getScore().longValue();

                // DB 업데이트
                showInfoRepository.findById(showInfoId).ifPresent(showInfo -> {
                    showInfo.setViewCount(viewCount);
                    showInfoRepository.save(showInfo);
                });

                syncCount++;

            } catch (Exception e) {
                log.error("개별 동기화 실패: {}", tuple.getValue(), e);
            }
        }

        log.info("장르 {} 동기화 완료: {} 건", genreId, syncCount);
    }

    /**
     * 수동 동기화 (테스트용)
     */
    @Transactional
    public void manualSync() {
        log.info("=== 수동 동기화 시작 ===");
        syncViewCountsToDatabase();
    }

    /**
     * 특정 공연의 조회수만 동기화 (실시간)
     */
    @Transactional
    public void syncSingleShowInfo(Long genreId, Long showInfoId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String key = DAILY_VIEW_KEY_PREFIX + genreId + ":" + today;

        Double score = redisTemplate.opsForZSet().score(key, showInfoId.toString());

        if (score != null) {
            showInfoRepository.findById(showInfoId).ifPresent(showInfo -> {
                showInfo.setViewCount(score.longValue());
                showInfoRepository.save(showInfo);
                log.info("개별 동기화 완료: showInfoId={}, viewCount={}", showInfoId, score.longValue());
            });
        }
    }
}
