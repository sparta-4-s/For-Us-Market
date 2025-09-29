package com.sparta.forusmarket.domain.user.service;

import com.sparta.forusmarket.domain.user.dto.response.UserResponse;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.exception.InvalidUserException;
import com.sparta.forusmarket.domain.user.exception.UserErrorCode;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserByIdU(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserException(UserErrorCode.INVALID_USER));
        return UserResponse.from(user);
    }
}
