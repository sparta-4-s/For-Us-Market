package com.sparta.forusmarket.domain.auth.dto.response;

import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import lombok.Builder;

@Builder
public record SignupResponse(Long id, String email, String name, Address address) {

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .address(user.getAddress())
                .build();
    }
}
