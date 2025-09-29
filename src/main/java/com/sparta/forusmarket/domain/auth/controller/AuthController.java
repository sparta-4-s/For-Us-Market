package com.sparta.forusmarket.domain.auth.controller;

import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.common.security.dto.TokenResponse;
import com.sparta.forusmarket.common.security.service.RefreshTokenService;
import com.sparta.forusmarket.common.security.utils.CookieUtil;
import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.auth.dto.request.LoginRequest;
import com.sparta.forusmarket.domain.auth.dto.request.SignupRequest;
import com.sparta.forusmarket.domain.auth.dto.request.WithdrawRequest;
import com.sparta.forusmarket.domain.auth.dto.response.LoginResponse;
import com.sparta.forusmarket.domain.auth.dto.response.SignupResponse;
import com.sparta.forusmarket.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.created(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                            HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        cookieUtil.addHttpOnlyCookie(response, tokenResponse.refreshToken());

        return ApiResponse.success(LoginResponse.of(tokenResponse.accessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@CookieValue("refreshToken") String refreshToken,
                                                 HttpServletResponse response) {
        TokenResponse tokenResponse = refreshTokenService.reissueTokens(
                        refreshToken)
                .orElseThrow(() -> new SecurityException("유효하지 않은 Refresh Token입니다."));

        cookieUtil.addHttpOnlyCookie(response, tokenResponse.refreshToken());
        return ResponseEntity.ok(LoginResponse.of(tokenResponse.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader,
                                                    @CookieValue("refreshToken") String refreshToken,
                                                    HttpServletResponse response) {
        String accessToken = jwtUtil.substringToken(authorizationHeader);

        authService.logout(accessToken, refreshToken);
        cookieUtil.deleteCookie(response);
        return ApiResponse.noContent();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal Long userId,
                                                      @Valid @RequestBody WithdrawRequest withdrawRequest,
                                                      @CookieValue("refreshToken") String refreshToken) {
        authService.withdraw(userId, withdrawRequest, refreshToken);
        return ApiResponse.noContent();
    }
}
