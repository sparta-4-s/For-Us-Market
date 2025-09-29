package com.sparta.forusmarket.common.security.service;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import com.sparta.forusmarket.common.security.dto.TokenResponse;
import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.user.dto.response.UserResponse;
import com.sparta.forusmarket.domain.user.service.UserService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityProperties jwtSecurityProperties;
    private final JwtUtil jwtUtil;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final UserService userService;

    public String saveToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        String key = buildKey(refreshToken);
        long timeout = jwtSecurityProperties.getToken().getRefreshExpiration();

        redisTemplate.opsForValue().set(key, String.valueOf(userId), timeout, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    @Transactional
    public Optional<TokenResponse> reissueTokens(String refreshToken) {
        String key = buildKey(refreshToken);

        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            return Optional.empty();
        }

        redisTemplate.delete(key);

        Long userId = Long.parseLong(userIdStr);
        UserResponse userResponse = userService.getUserByIdU(userId);

        String newAccessToken = jwtUtil.createToken(userResponse.id(), userResponse.email());
        String newRefreshToken = refreshTokenIssuer.createToken(userId);

        return Optional.of(TokenResponse.of(newAccessToken, newRefreshToken));
    }

    public void deleteToken(Long userId) {
        String refreshPrefix = jwtSecurityProperties.getToken().getRefreshPrefix();
        String key = refreshPrefix + userId;
        redisTemplate.delete(key);
    }

    private String buildKey(String refreshToken) {
        return jwtSecurityProperties.getToken().getRefreshPrefix() + refreshToken;
    }
}
