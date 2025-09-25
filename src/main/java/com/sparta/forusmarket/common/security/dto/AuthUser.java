package com.sparta.forusmarket.common.security.dto;

import com.sparta.forusmarket.common.enums.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record AuthUser(
        Long userId,
        String email,
        Collection<? extends GrantedAuthority> authorities) {
    public AuthUser(Long userId, String email, UserRole role) {
        this(userId, email, List.of(new SimpleGrantedAuthority(role.name())));
    }
}