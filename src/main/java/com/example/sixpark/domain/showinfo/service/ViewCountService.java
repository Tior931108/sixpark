package com.example.sixpark.domain.showinfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Key 형식
    private static final String DAILY_VIEW_KEY_PREFIX = "views:daily:genre:";  // views:daily:genre:{genreId}:{date}
    private static final String WEEKLY_VIEW_KEY_PREFIX = "views:weekly:genre:"; // views:weekly:genre:{genreId}:{weekStart}

    /**
     * 공연 조회수 증가 (일간)
     */
    @Transactional
    public void incrementDailyView(Long genreId, Long showInfoId) {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String key = DAILY_VIEW_KEY_PREFIX + genreId + ":" + date;

        redisTemplate.opsForZSet().incrementScore(key, showInfoId.toString(), 1);

        // TTL 설정 (7일 후 자동 삭제)
        redisTemplate.expire(key, 7, java.util.concurrent.TimeUnit.DAYS);
    }

    /**
     * 공연 조회수 증가 (주간)
     */
    @Transactional
    public void incrementWeeklyView(Long genreId, Long showInfoId) {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1); // 월요일
        String weekStartStr = weekStart.format(DateTimeFormatter.ISO_DATE);
        String key = WEEKLY_VIEW_KEY_PREFIX + genreId + ":" + weekStartStr;

        redisTemplate.opsForZSet().incrementScore(key, showInfoId.toString(), 1);

        // TTL 설정 (30일 후 자동 삭제)
        redisTemplate.expire(key, 30, java.util.concurrent.TimeUnit.DAYS);
    }

    /**
     * 일간 TOP N 조회
     */
    @Transactional(readOnly = true)
    public Set<ZSetOperations.TypedTuple<Object>> getDailyTopN(Long genreId, int n) {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String key = DAILY_VIEW_KEY_PREFIX + genreId + ":" + date;

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, n - 1);
    }

    /**
     * 주간 TOP N 조회
     */
    @Transactional(readOnly = true)
    public Set<ZSetOperations.TypedTuple<Object>> getWeeklyTopN(Long genreId, int n) {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
        String weekStartStr = weekStart.format(DateTimeFormatter.ISO_DATE);
        String key = WEEKLY_VIEW_KEY_PREFIX + genreId + ":" + weekStartStr;

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, n - 1);
    }

    /**
     * 랜덤 조회수 생성 (테스트용)
     */
    @Transactional
    public void generateRandomViews(Long genreId, List<Long> showInfoIds) {
        Random random = new Random();

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        LocalDate weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        String weekStartStr = weekStart.format(DateTimeFormatter.ISO_DATE);

        String dailyKey = DAILY_VIEW_KEY_PREFIX + genreId + ":" + today;
        String weeklyKey = WEEKLY_VIEW_KEY_PREFIX + genreId + ":" + weekStartStr;

        log.info("랜덤 조회수 생성 시작: genreId={}, dailyKey={}, weeklyKey={}",
                genreId, dailyKey, weeklyKey);

        // 기존 데이터 삭제 (덮어쓰기 모드)
        Boolean dailyExists = redisTemplate.hasKey(dailyKey);
        Boolean weeklyExists = redisTemplate.hasKey(weeklyKey);

        if (dailyExists) {
            redisTemplate.delete(dailyKey);
            log.warn("기존 일간 데이터 삭제: {}", dailyKey);
        }

        if (weeklyExists) {
            redisTemplate.delete(weeklyKey);
            log.warn("기존 주간 데이터 삭제: {}", weeklyKey);
        }

        for (Long showInfoId : showInfoIds) {
            // 일간 조회수: 0 ~ 1000
            int dailyViews = random.nextInt(1000);
            redisTemplate.opsForZSet().add(dailyKey, showInfoId.toString(), dailyViews);

            // 주간 조회수: 0 ~ 5000
            int weeklyViews = random.nextInt(5000);
            redisTemplate.opsForZSet().add(weeklyKey, showInfoId.toString(), weeklyViews);
        }

        // TTL 설정
        redisTemplate.expire(dailyKey, 7, java.util.concurrent.TimeUnit.DAYS);
        redisTemplate.expire(weeklyKey, 30, java.util.concurrent.TimeUnit.DAYS);

        log.info("랜덤 조회수 생성 완료: genreId={}, showInfos={}", genreId, showInfoIds.size());
    }
}
