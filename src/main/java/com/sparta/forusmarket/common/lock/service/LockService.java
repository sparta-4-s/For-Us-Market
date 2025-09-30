package com.sparta.forusmarket.common.lock.service;

import com.sparta.forusmarket.common.lock.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockService {
    private final RedisLockRepository redisLockRepository;

    public void lock(Long id) {
        while (!redisLockRepository.lock(id)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unlock(Long id) {
        redisLockRepository.unlock(id);
    }
}
