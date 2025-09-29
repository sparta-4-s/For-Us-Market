package com.sparta.forusmarket.domain.hotKeywords.service;

import com.sparta.forusmarket.domain.hotKeywords.dto.HotKeywordsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HotKeywordsService {
    private final RedisTemplate<String, Object> redisTemplate;

    public HotKeywordsResponse getHotKeywords() {
        String key = "product:ranking";
        Set<Object> top5 = redisTemplate.opsForZSet().reverseRange(key, 0, 4);
        return HotKeywordsResponse.of(top5);
    }
}
