package com.example.docconneting.common.aop;

import com.example.docconneting.common.annotation.DistributedLock;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.parser.CustomSpringELParser;
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

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "Lock:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.docconneting.common.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        String lockKey = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.value());
        log.info("lockKey : {}", lockKey);

        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
            if (!lockable) {
                log.info("Lock 획득 실패 = {}", lockKey);
                throw new ClientException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

//            return aopForTransaction.proceed(joinPoint);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            log.error("락 획득 중 인터럽트 발생", e);
            throw e;
        } finally {
            log.info("락 해제");
            lock.unlock();
        }
    }
}
