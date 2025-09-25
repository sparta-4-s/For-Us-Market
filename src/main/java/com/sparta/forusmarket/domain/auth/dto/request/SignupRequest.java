package com.sparta.forusmarket.domain.auth.dto.request;

import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignupRequest(
        @NotBlank
        String email,

        @NotBlank
        String name,

        @NotBlank
        String password,

        @NotBlank
        Address address) {

    public User toEntity(String encodedPassword) {
        return User.from(email, name, password, address);
    }
}
