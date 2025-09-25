package com.sparta.forusmarket.domain.auth.service;

import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.response.SignupResponse;
import com.sparta.forusmarket.domain.auth.exception.AuthErrorCode;
import com.sparta.forusmarket.domain.auth.exception.DuplicateEmailException;
import com.sparta.forusmarket.domain.user.entity.User;
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

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if (isDuplicateEmail(signupRequest.email())) {
            throw new DuplicateEmailException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        User user = signupRequest.toEntity(passwordEncoder.encode(signupRequest.password()));
        userRepository.save(user);
        return SignupResponse.from(user);
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
