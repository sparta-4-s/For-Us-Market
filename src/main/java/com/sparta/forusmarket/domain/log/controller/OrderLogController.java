package com.sparta.forusmarket.domain.log.controller;

import com.sparta.forusmarket.common.response.ApiPageResponse;
import com.sparta.forusmarket.common.response.RestPage;
import com.sparta.forusmarket.domain.log.dto.response.OrderLogResponse;
import com.sparta.forusmarket.domain.log.service.OrderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderLogController {

    private final OrderLogService orderLogService;

    // 캐싱 x
    @GetMapping("/api/v1/orderlogs/{userId}")
    public ResponseEntity<ApiPageResponse<OrderLogResponse>> getUserLogs(
            @PathVariable Long userId,
            Pageable pageable
    ){
        return orderLogService.getUserLogs(userId, pageable);
    }

    // 캐싱 o
    @GetMapping("/api/v2/orderlogs/{userId}")
    public ResponseEntity<ApiPageResponse<OrderLogResponse>> getCachedUserLogs(
            @PathVariable Long userId,
            Pageable pageable
    ){
        RestPage<OrderLogResponse> page = orderLogService.getCachedUserLogs(userId, pageable);
        return ApiPageResponse.success(page);
    }
}
