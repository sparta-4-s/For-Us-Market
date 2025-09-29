package com.sparta.forusmarket.domain.hotKeywords.controller;

import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.domain.hotKeywords.dto.HotKeywordsResponse;
import com.sparta.forusmarket.domain.hotKeywords.service.HotKeywordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HotKeywordsController {
    private final HotKeywordsService hotKeywordsService;

    @GetMapping("/api/v1/hotkeyword")
    public ResponseEntity<ApiResponse<HotKeywordsResponse>> getHotKeywords() {
        return ApiResponse.success(hotKeywordsService.getHotKeywords());
    }
}
