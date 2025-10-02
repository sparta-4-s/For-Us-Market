package com.sparta.forusmarket.common.config;


import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import com.sparta.forusmarket.common.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtSecurityProperties jwtSecurityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        SecurityContextHolderAwareRequestFilter.class)

                // JWT 사용 시 불필요한 기능들 비활성화
                .formLogin(AbstractHttpConfigurer::disable)      // [SSR] 서버가 로그인 HTML 폼 렌더링
                .anonymous(AbstractHttpConfigurer::disable)      // 미인증 사용자를 익명으로 처리
                .httpBasic(AbstractHttpConfigurer::disable)      // [SSR] 인증 팝업
                .logout(AbstractHttpConfigurer::disable)         // [SSR] 서버가 세션 무효화 후 리다이렉트
                .rememberMe(AbstractHttpConfigurer::disable)     // 서버가 쿠키 발급하여 자동 로그인

                .authorizeHttpRequests(auth -> auth
                        // WHITE_LIST 허용
                        .requestMatchers(jwtSecurityProperties.getSecret().getWhiteList().toArray(new String[0]))
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}
