package com.example.docconneting.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    // 락의 이름
    String value();

    // 락의 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // 락 획득을 시도하는 최대 시간 (ms)
    long waitTime() default 5L;

    // 락을 획득한 후, 점유하는 최대 시간 (ms)
    long leaseTime() default 3L;
}
