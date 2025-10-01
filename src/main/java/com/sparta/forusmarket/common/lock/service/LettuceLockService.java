package com.sparta.forusmarket.common.lock.service;

import com.sparta.forusmarket.common.lock.repository.LettuceLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LettuceLockService {
    private final LettuceLockRepository redisLockRepository;

    public boolean tryLock(String key, String uuid, long timeoutMs, int tryTimeMs, long ttlMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (redisLockRepository.lock(key, uuid, ttlMs)) {
                return true;
            }
            Thread.sleep(tryTimeMs);
        }
        return false;
    }

    public void unlock(String key, String uuid) {
        redisLockRepository.unlock(key, uuid);
    }
}
