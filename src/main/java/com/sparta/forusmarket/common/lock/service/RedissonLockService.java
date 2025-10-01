package com.sparta.forusmarket.common.lock.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService {
    private final RedissonClient redissonClient;

    public RLock tryLock(String key, long waitTimeMs, long leaseTimeS) {
        RLock lock = redissonClient.getLock(key);

        try {
            boolean isLocked = lock.tryLock(waitTimeMs, leaseTimeS, TimeUnit.SECONDS);
            if (isLocked) {
                return lock;
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("lock interrupted", e);
        }
    }

    public void unlock(RLock lock) {
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
