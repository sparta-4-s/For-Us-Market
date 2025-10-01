package com.sparta.forusmarket.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory cf) {
        var valueSerializer = new GenericJackson2JsonRedisSerializer(); // DTO 캐싱
        var base = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .prefixCacheNameWith("v7:"); // 캐시 네임스페이스 버저닝

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        perCache.put("product", base.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(perCache)
                .enableStatistics()
                .build();
    }
}