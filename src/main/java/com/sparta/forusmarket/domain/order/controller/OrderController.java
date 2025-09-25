package com.sparta.forusmarket.domain.order.controller;

import com.sparta.forusmarket.common.response.ApiPageResponse;
import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import com.sparta.forusmarket.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return ApiResponse.created(orderService.createOrder(orderRequest));
    }

    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable("orderId") Long orderId) {
        return ApiResponse.success(orderService.getOrderResponse(orderId));
    }

    @GetMapping("/api/v1/orders")
    public ResponseEntity<ApiPageResponse<OrderResponse>> getOrder(@PageableDefault Pageable pageable) {
        return ApiPageResponse.success(orderService.getOrderPage(pageable));
    }

    @PatchMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable("orderId") Long orderId) {
        return ApiResponse.success(orderService.cancelOrder(orderId));
    }

    @PostMapping("/api/v1/data")
    public ResponseEntity<ApiResponse<Void>> createData() {
        orderService.createData();
        return ApiResponse.noContent();
    }
}
