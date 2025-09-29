package com.sparta.forusmarket.common.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtSecurityProperties {

    private Secret secret;
    private Token token;

    @Getter
    @AllArgsConstructor
    public static class Secret {
        private String aesKey;
        private String key;
        private List<String> whiteList;
    }

    @Getter
    @AllArgsConstructor
    public static class Token {
        private String prefix;
        private String refreshPrefix;
        private String blackListPrefix;
        private long expiration;
        private long refreshExpiration;
        private long blackListExpiration;
    }
}
