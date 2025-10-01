package com.sparta.forusmarket.common.lock.service;

import com.sparta.forusmarket.common.lock.repository.LettuceLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LettuceLockService {
    private final LettuceLockRepository redisLockRepository;

    public boolean tryLock(String key, String uuid, long timeoutMs, int tryTimeMs, long ttlMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (redisLockRepository.lock(key, uuid, ttlMs)) {
                return true;
            }
            try {
                Thread.sleep(tryTimeMs);
            } catch (InterruptedException e) {
                // 스레드 강제 종료
                Thread.currentThread().interrupt();
                throw new RuntimeException("lock interrupted");
            }
        }
        return false;

    }

    public Boolean unlock(String key, String uuid) {
        return redisLockRepository.unlock(key, uuid);
    }
}
