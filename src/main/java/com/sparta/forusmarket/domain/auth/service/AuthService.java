package com.sparta.forusmarket.domain.auth.service;

import com.sparta.forusmarket.domain.auth.dto.request.UserSignupRequest;
import com.sparta.forusmarket.domain.auth.dto.response.UserSignupResponse;
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
    public UserSignupResponse signup(UserSignupRequest userSignupRequest) {
        if (isDuplicateEmail(userSignupRequest.email())) {
            throw new DuplicateEmailException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        User user = userSignupRequest.toEntity(passwordEncoder.encode(userSignupRequest.password()));
        userRepository.save(user);
        return UserSignupResponse.from(user);
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
