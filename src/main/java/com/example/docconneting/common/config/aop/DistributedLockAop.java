package com.example.docconneting.common.config.aop;

import com.example.docconneting.common.config.annotation.DistributedLock;
import com.example.docconneting.common.config.parser.CustomSpringELParser;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.docconneting.common.config.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
                joinPoint.getArgs(), distributedLock.value());
        log.info("[AOP 진입] 분산 락 대상 메서드: {} | key: {}", method.getName(), key);

        RLock lock = redissonClient.getLock(key);

        try {
            boolean available = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                log.warn("[LOCK 실패] key: {}", key);
                throw new ClientException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }
            log.info("[LOCK 성공] key: {}", key);
//            return aopForTransaction.proceed(joinPoint);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            log.error("락 획득 중 인터럽트 발생", e);
            throw e;
        } finally {
            try {
                lock.unlock();
            } catch (IllegalStateException e) {
                log.info("Redisson Lock Already Unlocked");
            }
        }
    }
}
