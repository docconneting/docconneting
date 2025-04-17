package com.example.docconneting.domain.coupon.lock;

public interface DistributedLockManager {
    void executeWithLock(Long key, Runnable task) throws InterruptedException;
}
