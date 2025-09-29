package com.sparta.forusmarket.common.security.service;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityProperties jwtSecurityProperties;

    public void addToken(String accessToken, long remainingExpiration) {
        String key = buildKey(accessToken);
        redisTemplate.opsForValue().set(key, "LOGGED_OUT", remainingExpiration, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String accessToken) {
        String key = buildKey(accessToken);
        return redisTemplate.hasKey(key);
    }

    private String buildKey(String accessToken) {
        return jwtSecurityProperties.getToken().getBlackListPrefix() + accessToken;
    }
}
