package com.example.docconneting.domain.coupon.lock;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonDistributedLockManager implements DistributedLockManager {

    private final RedissonClient redissonClient;
    private static final String LOCK_KEY_PREFIX = "coupon-lock";

    @Override
    public void executeWithLock(Long key, Runnable task) throws InterruptedException {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock fairLock = redissonClient.getFairLock(lockKey);

        // 10초 이내 락 획득 시도, 5초 후 타임아웃 설정
        if (fairLock.tryLock(10, 5, TimeUnit.SECONDS)) {
            try {
                //Runnable task로 받은 실행 로직 여기서 시작
                task.run();
            } finally {
                fairLock.unlock();
            }
        } else {
            throw new ServerException(ErrorCode.LOCK_ACQUISITION_FAILED);
        }
    }
}
