package com.sparta.forusmarket.domain.order.service;

import com.sparta.forusmarket.common.lock.aop.LettuceLock;
import com.sparta.forusmarket.common.lock.aop.RedissonLock;
import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderLockService {
    private final OrderService orderService;

    @LettuceLock(key = "#orderRequest.getProductId()")
    public OrderResponse createOrderWithLettuceLock(OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @RedissonLock(key = "#orderRequest.getProductId()")
    public OrderResponse createOrderWithRedissonLock(OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}