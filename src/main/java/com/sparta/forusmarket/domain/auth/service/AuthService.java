package com.sparta.forusmarket.domain.auth.service;

import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.auth.dto.request.LoginRequest;
import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.request.WithdrawRequest;
import com.sparta.forusmarket.domain.auth.dto.response.LoginResponse;
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

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if (isDuplicateEmail(signupRequest.email())) {
            throw new DuplicateEmailException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        User user = signupRequest.toEntity(passwordEncoder.encode(signupRequest.password()));
        return SignupResponse.from(userRepository.save(user));
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidEmailOrPasswordException(AuthErrorCode.INVALID_EMAIL_OR_PASSWORD));

        if (!isMatchedPassword(loginRequest.password(), user.getPassword())) {
            throw new InvalidEmailOrPasswordException(AuthErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user.getId(), loginRequest.email());
        return LoginResponse.of(accessToken);
    }

    // 추후 블랙리스트 방식으로 로그아웃 구현 예정

    @Transactional
    public void withdraw(Long userId, WithdrawRequest withdrawRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserException(UserErrorCode.INVALID_USER));

        if (!isMatchedPassword(withdrawRequest.password(), user.getPassword())) {
            throw new InvalidEmailOrPasswordException(AuthErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        userRepository.deleteById(userId);
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isMatchedPassword(String requestPassword, String password) {
        return passwordEncoder.matches(password, requestPassword);
    }
}
