//package com.example.docconneting.domain.coupon.aop;
//
//import com.example.docconneting.common.config.annotation.DistributedLock;
//import com.example.docconneting.common.config.aop.AopForTransaction;
//import com.example.docconneting.common.exception.constant.ErrorCode;
//import com.example.docconneting.common.exception.object.ServerException;
//import com.example.docconneting.domain.coupon.util.CustomSpringELParser;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DistributedLockAspect {
//
//    private static final String REDISSON_LOCK_PREFIX = "Lock:";
//
//    private final RedissonClient redissonClient;
//    private final AopForTransaction aopForTransaction;
//
//    @Around("@annotation(com.example.docconneting.common.config.annotation.DistributedLock)")
//    private Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
//
//        // 키 파싱
//        String key = REDISSON_LOCK_PREFIX +
//                CustomSpringELParser.getDynamicValue(
//                        signature.getParameterNames(),
//                        joinPoint.getArgs(),
//                        distributedLock.value()
//                );
//
//        RLock lock = redissonClient.getLock(key); // (1)
//
//        try {
//            boolean available = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
//            if (!available) {
//                log.warn("락 획득 실패 key: {}", key);
//                return false;
//            }
//
//            // 트랜잭션 보장 처리
//            return aopForTransaction.proceed(joinPoint);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new ServerException(ErrorCode.LOCK_INTERRUPTED);
//        } finally {
//            try {
//                lock.unlock();
//            } catch (IllegalStateException e) {
//                log.info("이미 락이 해제된 상태입니다. key: {}", key);
//            }
//        }
//    }
//
//
//    private String resolveKey(ProceedingJoinPoint joinPoint, String keyParamName) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String[] paramNames = signature.getParameterNames();
//        Object[] args = joinPoint.getArgs();
//
//        for (int i = 0; i < paramNames.length; i++) {
//            if (paramNames[i].equals(keyParamName)) {
//                return args[i].toString();
//            }
//        }
//        throw new IllegalArgumentException("해당 key 이름을 가진 파라미터를 찾을 수 없습니다.");
//    }
//}
