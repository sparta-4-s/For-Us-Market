package com.sparta.forusmarket.domain.auth.service;

import com.sparta.forusmarket.common.security.dto.TokenResponse;
import com.sparta.forusmarket.common.security.service.RedisBlacklistService;
import com.sparta.forusmarket.common.security.service.RefreshTokenService;
import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.auth.dto.request.LoginRequest;
import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.request.WithdrawRequest;
import com.sparta.forusmarket.domain.auth.dto.response.SignupResponse;
import com.sparta.forusmarket.domain.auth.exception.AuthErrorCode;
import com.sparta.forusmarket.domain.auth.exception.DuplicateEmailException;
import com.sparta.forusmarket.domain.auth.exception.InvalidEmailOrPasswordException;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.exception.InvalidUserException;
import com.sparta.forusmarket.domain.user.exception.UserErrorCode;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RedisBlacklistService redisBlacklistService;


    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if (isDuplicateEmail(signupRequest.email())) {
            throw new DuplicateEmailException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        User user = signupRequest.toEntity(passwordEncoder.encode(signupRequest.password()));
        return SignupResponse.from(userRepository.save(user));
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidEmailOrPasswordException(AuthErrorCode.INVALID_EMAIL_OR_PASSWORD));

        if (!isMatchedPassword(loginRequest.password(), user.getPassword())) {
            throw new InvalidEmailOrPasswordException(AuthErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user.getId(), user.getEmail());
        String refreshToken = refreshTokenService.saveToken(user.getId());

        return TokenResponse.of(accessToken, refreshToken);
    }

    public void logout(String accessToken, String refreshToken) {
        long remainingMillis = jwtUtil.getTokenRemainingMillis(accessToken);

        if (remainingMillis > 0) {
            redisBlacklistService.addToken(accessToken, remainingMillis);
        }

        if (refreshToken != null) {
            refreshTokenService.deleteToken(refreshToken);
        }
    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequest withdrawRequest, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserException(UserErrorCode.INVALID_USER));

        if (!isMatchedPassword(withdrawRequest.password(), user.getPassword())) {
            throw new InvalidEmailOrPasswordException(AuthErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        refreshTokenService.deleteToken(refreshToken);
        userRepository.deleteById(userId);
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean isMatchedPassword(String requestPassword, String password) {
        return passwordEncoder.matches(requestPassword, password);
    }
}
