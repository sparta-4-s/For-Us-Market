package com.sparta.forusmarket.common.lock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class LettuceLockRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(String key, String uuid, long ttl) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, uuid, Duration.ofMillis(ttl));
    }

    public Boolean unlock(String key, String uuid) {
        String luaScript =
                "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end";
        RedisScript<Long> script = RedisScript.of(luaScript, Long.class);

        Long deleteResult = redisTemplate.execute(script, Collections.singletonList(key), uuid);

        return deleteResult > 0;
    }
}
