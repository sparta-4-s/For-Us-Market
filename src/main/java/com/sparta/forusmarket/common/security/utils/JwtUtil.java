package com.sparta.forusmarket.common.security.utils;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final JwtSecurityProperties jwtSecurityProperties;

    private Key key;

    @PostConstruct
    public void init() {
        String secretKey = jwtSecurityProperties.getSecret().getKey();
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Long userId, String email) {
        Date date = new Date();
        String PREFIX = jwtSecurityProperties.getToken().getPrefix();
        long TOKEN_TIME = jwtSecurityProperties.getToken().getExpiration();

        return PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String substringToken(String tokenValue) {
        String PREFIX = jwtSecurityProperties.getToken().getPrefix();

        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(PREFIX)) {
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String prefix = jwtSecurityProperties.getToken().getPrefix();

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(prefix)) {
            return bearerToken.substring(prefix.length());
        }
        return null;
    }

    public long getTokenRemainingMillis(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();

            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}