package com.example.docconneting.domain.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key(); // 락에 사용할 key값 (지금은 쿠폰Id)

    TimeUnit tineUnit() default TimeUnit.SECONDS;

    long waitTime() default 30L;

    long leaseTime() default 30L;

}
