package com.sparta.forusmarket.domain.auth.controller;

import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.domain.auth.dto.request.UserSignupRequest;
import com.sparta.forusmarket.domain.auth.dto.response.UserSignupResponse;
import com.sparta.forusmarket.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(
            @Valid @RequestBody UserSignupRequest userSignupRequest) {
        UserSignupResponse userSignupResponse = authService.signup(userSignupRequest);
        return ApiResponse.created(userSignupResponse);
    }
}