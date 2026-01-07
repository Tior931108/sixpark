package com.example.sixpark.lock;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private static final long TTL = 3000; // 3초

    private final RedisLockRepository redisLockRepository;

    public <T> T executeWithLock(String key, Supplier<T> action) {
        String lockValue = UUID.randomUUID().toString();

        boolean locked = redisLockRepository.acquireLock(key, lockValue, TTL); // 🔒 락 획득
        log.info("락 획득 성공 여부: {}", locked);
        if (!locked) { // 락 획득 실패 시 즉시 예외 처리
            throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);
        }

        try {
            return action.get();
        } finally { // 락은 반드시 해제
            boolean unlocked = redisLockRepository.releaseLock(key, lockValue); // 🔓 락 해제
            log.info("락 해제 성공 여부: {}", unlocked);
        }
    }
}
