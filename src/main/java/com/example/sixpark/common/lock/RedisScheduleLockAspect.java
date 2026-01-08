package com.example.sixpark.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * @Scheduled 메서드가 동시에 실행되는 것을 방지하기 위한
 * 스케줄러 전용 분산 락 AOP
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisScheduleLockAspect {

    private final RedisTemplate<String, String> redisTemplate;

    @Around("@annotation(scheduleLock)")
    public Object applySchedulerLock(ProceedingJoinPoint joinPoint, RedisScheduleLock scheduleLock) throws Throwable {

        // 락 획득 로직, RedisTemplate 반환값 null-safe 처리, setIfAbsent() - 이미 락이 있으면 실패 → 단 하나만 성공, TTL 설정
        boolean locked = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(scheduleLock.key(), "locked", scheduleLock.ttl(), TimeUnit.MILLISECONDS));

        if (!locked) {
            log.info("[SchedulerLock] already running: {}", scheduleLock.key());
            return null;
        }

        // 비즈니스 로직에서 예외가 발생해도 락은 반드시 해제되어야 하기때문에 TTL이 있어도 바로 해제
        try {
            return joinPoint.proceed();  // 메서드 발생
        } finally {
            redisTemplate.delete(scheduleLock.key());
        }
    }

}
