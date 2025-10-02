package com.sparta.forusmarket.common.security.dto;

public record TokenResponse(String accessToken, String refreshToken) {
    public static TokenResponse of(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}
