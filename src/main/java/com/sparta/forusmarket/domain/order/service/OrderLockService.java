package com.sparta.forusmarket.domain.order.service;

import com.sparta.forusmarket.common.lock.aop.LettuceLock;
import com.sparta.forusmarket.common.lock.aop.RedissonLock;
import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    LockAcquisitionException.class,
                    CannotAcquireLockException.class,
                    PessimisticLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public OrderResponse createOderWithOptimisticLock(OrderRequest orderRequest) {
        return orderService.createOrderWithOptimisticLock(orderRequest);
    }

    @Recover
    public OrderResponse recover(ObjectOptimisticLockingFailureException e, OrderRequest orderRequest) {
        throw new IllegalArgumentException("Maximum retry attempts exceeded.", e);
    }
}