package com.example.sixpark.common.lock;

import com.example.sixpark.domain.seat.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * @RedisLock 어노테이션이 붙은 메서드 실행 전/후에 개입하여
 * Redis 분산 락을 획득 → 비즈니스 실행 → 락 해제까지 책임지는 AOP 클래스
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    // Redis 락의 실제 구현체 - Aspect 락의 정책만 알고, 구현은 락 서비스에서 진행
    // @RedisLock(key = "..") 에 들어오는 SpEL 표현식 파싱 - 파라미터 기반으로 Lock key 생성 (정확한 락 범위를 위함)
    private final LockService lockService;
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 락 메서드 실행 전/후 관여
     * @RedisLock 어노테이션이 붙어있는 메서드에만 관여
     */
    @Around("@annotation(redisLock)")
    public Object applyRedisLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) {

        // 어노테이션에 선언된 SpEL을 실제 값으로 반환
        String key = "lock:" + parseKey(joinPoint, redisLock.key());
        long ttl = redisLock.ttl();

        log.info("[RedisLock] try lock key = {}", key);

        // AOP에서는 실행만을 위한 로직이기 때문에 람다 식 반영 (재사용 가능)
        return lockService.executeWithLock(key, ttl, () -> {
            try {
                return joinPoint.proceed();  // 메서드 발생
            } catch (Throwable t) {
                throw new RuntimeException(t); // Supplier<T>에서 checked 예외를 허용하지 않음. -> runtime 적용
            }
        });

    }

    private String parseKey(ProceedingJoinPoint joinPoint, String spel) {

        // 메서드 정보 접근 - 파라미터 이름, 타입
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 어노테이션에서 SpEL 값에 #request, #id 등을 사용하기 위해 매핑 필요
        String[] paraNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // 위 변수 저장소, 바인딩 시점
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paraNames.length; i++) {
            context.setVariable(paraNames[i], args[i]); // 다음과 같은 형태가 가능하도록 함. @RedisLock(key = "'seat:' + #request.scheduleId")
        }

        // SpEL의 문자열을 실제 값으로 평가 후 최종적으로 Redis 키를 생성한다. 예시 : ["seat:123:45"]
        return parser.parseExpression(spel).getValue(context, String.class);
    }
}
