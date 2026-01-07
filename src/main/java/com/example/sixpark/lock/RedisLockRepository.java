package com.example.sixpark.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Lock 획득
     */
    public boolean acquireLock(String key, String value, long ttlMillis) {
        // setIfAbsent: 기존 키 존재 시 실패함 -> 딱 한 요청만 성공
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(ttlMillis));
        return Boolean.TRUE.equals(success); // success: 성공 → true / 실패 → false / 예외 → null
    }

    /**
     * Lock 해제
     */
    public boolean releaseLock(String key, String value) {
        // Lua Script 사용: 락을 건 주체만 해제할 수 있도록 보장
        String script = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                end
                return 0
                """;

        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), List.of(key), value);
        return result == 1L; // result: 성공 → 1 / 실패 → 0
    }
}
