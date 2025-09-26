package com.sparta.forusmarket.config;

import com.sparta.forusmarket.common.security.dto.AuthUser;
import com.sparta.forusmarket.common.security.filter.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockAuthUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        AuthUser authUser = new AuthUser(customUser.userId(), customUser.email());
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);
        context.setAuthentication(authentication);
        return context;
    }
}
