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
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Key 형식
    private static final String DAILY_VIEW_KEY_PREFIX = "views:daily:genre:";  // views:daily:genre:{genreId}:{date}
    private static final String WEEKLY_VIEW_KEY_PREFIX = "views:weekly:genre:"; // views:weekly:genre:{genreId}:{weekStart}
    private static final String DAILY_COUNT_KEY_PREFIX = "count:daily:";           // count:daily:{genreId}:{showInfoId}:{userId}
    private static final String WEEKLY_COUNT_KEY_PREFIX = "count:weekly:";         // count:weekly:{genreId}:{showInfoId}:{userId}

    private static final int MAX_DAILY_VIEWS = 20;   // 일간 최대 조회수
    private static final int MAX_WEEKLY_VIEWS = 150;  // 주간 최대 조회수

    /**
     * 공연 조회수 증가 (일간) + 어뷰징 방지 + 자정 지나면 삭제
     */
    @Transactional
    public boolean incrementDailyView(Long genreId, Long showInfoId, String identifier) {

        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String viewKey = DAILY_VIEW_KEY_PREFIX + genreId + ":" + date;
        String countKey = DAILY_COUNT_KEY_PREFIX + genreId + ":" + showInfoId + ":" + identifier;

        // 현재 주간 조회 횟수 확인
        Integer currentCountStr = (Integer) redisTemplate.opsForValue().get(countKey);
        int currentCount = currentCountStr != null ? currentCountStr : 0;

        // 20회 제한 체크
        if (currentCount >= MAX_DAILY_VIEWS) {
            log.debug("일간 조회 제한 초과: genreId={}, showInfoId={}, userId={}, count={}", genreId, showInfoId, identifier, currentCount);
            return false;
        }

        // 조회수 증가
        redisTemplate.opsForZSet().incrementScore(viewKey, showInfoId.toString(), 1);

        // 조회 횟수 증가 (원자적 연산)
        // redis는 싱글 스레드로 동작하기에
        // 한번에 하나의 명령만 처리 진행.
        // 명령이 실행 되는 동안 다른 명령이 끼어들 수 없음 > 원자성 보장함.
        Long newCount = redisTemplate.opsForValue().increment(countKey, 1);

        // 첫 조회 시 TTL 설정 (자정까지)
        if (newCount != null && newCount == 1) {
            // 자정 까지 남은초 계산 로직
            long secondsUntilMidnight = getSecondsUntilMidnight();
            redisTemplate.expire(countKey, secondsUntilMidnight, TimeUnit.SECONDS);
            redisTemplate.expire(viewKey, secondsUntilMidnight, TimeUnit.SECONDS);
        }

        log.debug("일간 조회수 증가: genreId={}, showInfoId={}, userId={}, count={}/{}", genreId, showInfoId, identifier, newCount, MAX_DAILY_VIEWS);

        return true;
    }

    /**
     * 공연 조회수 증가 (주간) + 어뷰징 방지
     */
    @Transactional
    public boolean incrementWeeklyView(Long genreId, Long showInfoId, String identifier) {

        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1); // 월요일
        String weekStartStr = weekStart.format(DateTimeFormatter.ISO_DATE);
        String viewKey = WEEKLY_VIEW_KEY_PREFIX + genreId + ":" + weekStartStr;
        String countKey = WEEKLY_COUNT_KEY_PREFIX + genreId + ":" + showInfoId + ":" + identifier;

        // 현재 주간 조회 횟수 확인
        Integer currentCountStr = (Integer) redisTemplate.opsForValue().get(countKey);
        int currentCount = currentCountStr != null ? currentCountStr : 0;

        // 150회 제한 체크
        if (currentCount >= MAX_WEEKLY_VIEWS) {
            log.debug("주간 조회 제한 초과: genreId={}, showInfoId={}, userId={}, count={}", genreId, showInfoId, identifier, currentCount);
            return false;
        }

        // 조회수 증가
        redisTemplate.opsForZSet().incrementScore(viewKey, showInfoId.toString(), 1);

        // 조회 횟수 증가
        Long newCount = redisTemplate.opsForValue().increment(countKey, 1);

        // 첫 조회 시 TTL 설정 (7일)
        if (newCount != null && newCount == 1) {
            redisTemplate.expire(countKey, 7, TimeUnit.DAYS);
            redisTemplate.expire(viewKey, 30, TimeUnit.DAYS);
        }

        log.debug("주간 조회수 증가: genreId={}, showInfoId={}, userId={}, count={}/{}", genreId, showInfoId, identifier, newCount, MAX_WEEKLY_VIEWS);

        return true;
    }

    // 자정 확인하는 로직 메소드
    private long getSecondsUntilMidnight() {

        // 현재 날짜 기준 내일 계산
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        // 자정 까지 남은초 계산 로직
        long midnightEpoch = tomorrow.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
        long nowEpoch = java.time.Instant.now().getEpochSecond();

        return midnightEpoch - nowEpoch;
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
