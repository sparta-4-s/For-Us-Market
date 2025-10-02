package com.sparta.forusmarket.domain.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import com.sparta.forusmarket.common.security.service.RedisBlacklistService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class RedisBlacklistServiceTest {

    @InjectMocks
    private RedisBlacklistService redisBlacklistService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private JwtSecurityProperties jwtSecurityProperties;
    @Mock
    private JwtSecurityProperties.Token token;

    @Test
    void 블랙리스트_토큰_추가에_성공한다() throws Exception {
        // given
        String accessToken = "accessToken";
        long expiration = 5000L;

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(jwtSecurityProperties.getToken()).willReturn(token);
        given(token.getBlackListPrefix()).willReturn("Blacklist");

        // when
        redisBlacklistService.addToken(accessToken, expiration);

        // then
        then(valueOperations).should().set(
                eq("Blacklist" + accessToken),
                eq("LOGGED_OUT"),
                eq(expiration),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void 블랙리스트_토큰_존재_여부_확인에_성공한다() throws Exception {
        // given
        String accessToken = "accessToken";

        given(jwtSecurityProperties.getToken()).willReturn(token);
        given(token.getBlackListPrefix()).willReturn("Blacklist");
        given(redisTemplate.hasKey("Blacklist" + accessToken)).willReturn(true);

        // when
        boolean result = redisBlacklistService.isTokenBlacklisted(accessToken);

        // then
        assertThat(result).isTrue();
        then(redisTemplate).should().hasKey("Blacklist" + accessToken);
    }
}
