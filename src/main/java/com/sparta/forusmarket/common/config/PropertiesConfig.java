package com.sparta.forusmarket.common.config;

import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtSecurityProperties.class)
public class PropertiesConfig {
}
