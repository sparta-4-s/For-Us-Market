package com.sparta.forusmarket.domain.auth.dto.request;

import com.sparta.forusmarket.domain.user.dto.AddressDto;
import com.sparta.forusmarket.domain.user.entity.User;
import jakarta.validation.Valid;
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

        @Valid
        AddressDto addressDto) {

    public User toEntity(String encodedPassword) {
        return User.from(email, name, password, addressDto);
    }
}
