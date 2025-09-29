package com.sparta.forusmarket.domain.auth.sevice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.sparta.forusmarket.common.security.dto.TokenResponse;
import com.sparta.forusmarket.common.security.service.RedisBlacklistService;
import com.sparta.forusmarket.common.security.service.RefreshTokenService;
import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.auth.dto.request.LoginRequest;
import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.request.WithdrawRequest;
import com.sparta.forusmarket.domain.auth.dto.response.SignupResponse;
import com.sparta.forusmarket.domain.auth.service.AuthService;
import com.sparta.forusmarket.domain.user.dto.AddressDto;
import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private RedisBlacklistService redisBlacklistService;

    @Test
    public void 회원가입에_성공한다() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest(
                "john@example.com",
                "John Doe",
                "Password123!",
                new AddressDto(
                        "김포시",
                        "풍무동 서해아파트 102-23213",
                        "123-345"));
        User user = new User(
                "john@example.com",
                "John Doe",
                "Password123!",
                new Address(
                        "김포시",
                        "풍무동 서해아파트 102-23213",
                        "123-345"));

        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        SignupResponse signupResponse = authService.signup(signupRequest);

        //then
        assertThat(signupResponse).isNotNull();
        assertThat(signupResponse.id()).isEqualTo(1L);
        assertThat(signupResponse.name()).isEqualTo("John Doe");
        assertThat(signupResponse.address().getCity()).isEqualTo("김포시");
        assertThat(signupResponse.address().getStreet()).isEqualTo("풍무동 서해아파트 102-23213");
        assertThat(signupResponse.address().getZipcode()).isEqualTo("123-345");
    }

    @Test
    public void 로그인에_성공하여_Token을_반환한다() throws Exception {
        //given
        String rawPassword = "test1234!";
        LoginRequest loginRequest = new LoginRequest("test@test.com", rawPassword);

        User user = new User(
                "test@test.com",
                "test",
                "test1234!",
                new Address(
                        "tc",
                        "ts",
                        "123-4"));

        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtUtil.createToken(anyLong(), anyString())).willReturn("accessToken");
        given(refreshTokenService.saveToken(user.getId())).willReturn("refreshToken");

        //when
        TokenResponse tokenResponse = authService.login(loginRequest);

        //then
        assertThat(tokenResponse.accessToken()).isEqualTo("accessToken");
        assertThat(tokenResponse.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    public void 로그아웃에_성공한다() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        given(jwtUtil.getTokenRemainingMillis(accessToken)).willReturn(10000L);
        willDoNothing().given(redisBlacklistService).addToken(accessToken, 10000L);
        willDoNothing().given(refreshTokenService).deleteToken(refreshToken);

        //when
        authService.logout(accessToken, refreshToken);

        //then
        then(redisBlacklistService).should().addToken(accessToken, 10000L);
        then(refreshTokenService).should().deleteToken(refreshToken);
    }

    @Test
    public void 회원탈퇴에_성공한다() throws Exception {
        // given
        String rawPassword = "test1234!";
        WithdrawRequest withdrawRequest = new WithdrawRequest(rawPassword);
        String refreshToken = "zsvakfdgnl";

        User user = new User(
                "test@test.com",
                "test",
                "test1234!",
                new Address(
                        "tc",
                        "ts",
                        "123-4"));

        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when
        authService.withdraw(user.getId(), withdrawRequest, refreshToken);

        // then
        then(refreshTokenService).should().deleteToken(refreshToken);
        then(userRepository).should().deleteById(user.getId());
    }
}
