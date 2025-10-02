package com.sparta.forusmarket.common.security.service;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenIssuer {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityProperties jwtSecurityProperties;

    public String createToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();

        String refreshPrefix = jwtSecurityProperties.getToken().getRefreshPrefix();
        String key = refreshPrefix + refreshToken;

        long timeoutMs = jwtSecurityProperties.getToken().getExpiration();
        redisTemplate.opsForValue()
                .set(key, String.valueOf(userId), timeoutMs, TimeUnit.MILLISECONDS);

        return refreshToken;
    }
}
