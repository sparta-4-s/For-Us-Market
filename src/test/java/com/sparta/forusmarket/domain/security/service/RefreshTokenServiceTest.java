package com.sparta.forusmarket.domain.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import com.sparta.forusmarket.common.security.dto.TokenResponse;
import com.sparta.forusmarket.common.security.service.RefreshTokenIssuer;
import com.sparta.forusmarket.common.security.service.RefreshTokenService;
import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.user.dto.response.UserResponse;
import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.service.UserService;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private JwtSecurityProperties jwtSecurityProperties;
    @Mock
    private JwtSecurityProperties.Token token;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @Mock
    private RefreshTokenIssuer refreshTokenIssuer;

    @Test
    void 토큰_저장에_성공하여_토큰을_반환한다() throws Exception {
        //given
        Long userId = 1L;
        long expirationTime = 10000L;

        // 모든 테스트 전에 redisTemplate.opsForValue()가 valueOperations Mock을 반환하도록 설정
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        given(jwtSecurityProperties.getToken()).willReturn(token);
        given(token.getRefreshPrefix()).willReturn("refresh");
        given(token.getRefreshExpiration()).willReturn(expirationTime);

        //when
        String refreshToken = refreshTokenService.saveToken(userId);

        //then
        assertThat(refreshToken).isNotNull();
        then(valueOperations).should().set(
                eq("refresh" + refreshToken),
                eq(String.valueOf(userId)),
                eq(expirationTime),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void 토큰_재발급에_성공하여_토큰을_반환한다() throws Exception {
        //given
        Long userId = 1L;
        String refreshToken = "refreshToken";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("refresh" + refreshToken)).willReturn(String.valueOf(userId));

        given(jwtSecurityProperties.getToken()).willReturn(token);
        given(token.getRefreshPrefix()).willReturn("refresh");

        UserResponse userResponse = new UserResponse(
                userId,
                "expirationTime",
                "refreshToken",
                new Address(
                        "tc",
                        "ts",
                        "tz"
                )
        );

        given(userService.getUserByIdU(userId)).willReturn(userResponse);
        given(jwtUtil.createToken(userId, userResponse.email())).willReturn("newAccessToken");
        given(refreshTokenIssuer.createToken(userId)).willReturn("newRefreshToken");

        //when
        Optional<TokenResponse> tokenResponse = refreshTokenService.reissueTokens(refreshToken);

        //then
        assertThat(tokenResponse).isPresent();
        assertThat(tokenResponse.get().accessToken()).isEqualTo("newAccessToken");
        assertThat(tokenResponse.get().refreshToken()).isEqualTo("newRefreshToken");
        then(redisTemplate).should().delete("refresh" + refreshToken);
    }

    @Test
    void 토큰_삭제에_성공한다() throws Exception {
        //given
        String refreshToken = "refreshToken";
        String key = "refresh" + refreshToken;

        given(jwtSecurityProperties.getToken()).willReturn(token);
        given(token.getRefreshPrefix()).willReturn("refresh");

        //when
        refreshTokenService.deleteToken(refreshToken);

        //then
        then(redisTemplate).should().delete(key);
    }
}
