package com.sparta.forusmarket.domain.auth.controller;

import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.response.SignupResponse;
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
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(signupRequest);
        return ApiResponse.created(signupResponse);
    }
}
