package com.sparta.forusmarket.domain.auth.controller;

import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.domain.auth.dto.request.LoginRequest;
import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.request.WithdrawRequest;
import com.sparta.forusmarket.domain.auth.dto.response.LoginResponse;
import com.sparta.forusmarket.domain.auth.dto.response.SignupResponse;
import com.sparta.forusmarket.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.created(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.created(authService.login(loginRequest));
    }

    // 추후 블랙리스트 방식으로 로그아웃 구현 예정

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal Long userId,
                                                    @Valid @RequestBody WithdrawRequest withdrawRequest) {
        authService.withdraw(userId, withdrawRequest);
        return ApiResponse.noContent();
    }
}
