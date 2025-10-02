package com.sparta.forusmarket.common.security.utils;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtSecurityProperties jwtSecurityProperties;
    private String refreshPrefix;

    public void addHttpOnlyCookie(HttpServletResponse response, String value) {
        refreshPrefix = jwtSecurityProperties.getToken().getRefreshPrefix();
        long refreshExpiration = jwtSecurityProperties.getToken().getRefreshExpiration();

        ResponseCookie cookie = ResponseCookie.from(refreshPrefix, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshExpiration / 1000)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response) {
        refreshPrefix = jwtSecurityProperties.getToken().getRefreshPrefix();

        ResponseCookie cookie = ResponseCookie.from(refreshPrefix, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String getCookieValue(HttpServletRequest request) {
        refreshPrefix = jwtSecurityProperties.getToken().getRefreshPrefix();

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(refreshPrefix)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
