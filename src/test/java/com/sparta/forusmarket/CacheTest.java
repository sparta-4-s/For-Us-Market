package com.sparta.forusmarket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class CacheTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void checkCache() {
        Object cached = redisTemplate.opsForValue().get("product::all");
        System.out.println(cached);
    }
}
