package com.sparta.forusmarket.domain.log.aop;

import com.sparta.forusmarket.domain.log.entity.OrderLog;
import com.sparta.forusmarket.domain.log.service.OrderLogService;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OrderLogAspect {

    private final OrderLogService orderLogService;

    @Around("execution( * com.sparta.forusmarket.domain.order.service.OrderService.createOrder(..))" )
    public Object createOrder(ProceedingJoinPoint pjp) throws Throwable {

        Long userId = null;
        Long orderId = null;
        Long productId = null;
        Integer quantity = null;
        BigDecimal price = null;
        String message = null;

        log.info("[AUDIT-BEFORE] method={}", pjp.getSignature().toShortString());

        try {
            Object result = pjp.proceed();

            if (result instanceof OrderResponse orderResponse) {
                orderId = orderResponse.getId();
                userId = orderResponse.getUserId();
                productId = orderResponse.getProductId();
                quantity = orderResponse.getQuantity();
                price = orderResponse.getPrice();

                log.info("[AUDIT-AFTER] return={}", result);
                if(orderResponse.getStatus() == OrderStatus.SUCCESS) {
                    OrderLog success = OrderLog.success(orderId, userId, productId, quantity, price);
                    successRegistration(success);
                }else{
                    OrderLog fail = OrderLog.fail("Out of Stock");
                    failRegistration(fail);
                }
            }
            return result;
        } catch (Throwable ex) {
            log.error("[AUDIT-ERROR] op={}, ex={}", ex.toString(), ex);
            OrderLog fail = OrderLog.fail(ex.getMessage().toString());
            failRegistration(fail);
            throw ex;
        }
    }

    @Around("execution( * com.sparta.forusmarket.domain.order.service.OrderService.cancelOrder(..))" )
    public Object cancelOrder(ProceedingJoinPoint pjp) throws Throwable {

        Long userId = null;
        Long orderId = null;
        Long productId = null;
        Integer quantity = null;
        BigDecimal price = null;
        String message = null;

        log.info("[AUDIT-BEFORE] method={}", pjp.getSignature().toShortString());

        try {
            Object result = pjp.proceed();

            if (result instanceof OrderResponse orderResponse) {
                orderId = orderResponse.getId();
                userId = orderResponse.getUserId();
                productId = orderResponse.getProductId();
                quantity = orderResponse.getQuantity();
                price = orderResponse.getPrice();

                log.info("[AUDIT-AFTER] return={}", result);
                OrderLog cancel = OrderLog.cancel(orderId, userId, productId, quantity, price, message);
                cancleRegistration(cancel);
            }
            return result;
        } catch (Throwable ex) {
            log.error("[AUDIT-ERROR] op={}, ex={}", ex.toString(), ex);
            OrderLog fail = OrderLog.fail(ex.getMessage().toString());
            failRegistration(fail);
            throw ex;
        }
    }

    private void successRegistration(OrderLog orderLog) throws Exception{
        try{
            orderLogService.saveLog(orderLog);
        } catch (Exception logException) {
            log.error("로그 저장 실패: {}", logException.getMessage());
        }
    }

    private void failRegistration(OrderLog orderLog) throws Exception {
        try {
            orderLogService.saveLog(orderLog);
        } catch (Exception logException) {
            log.error("로그 저장 실패: {}", logException.getMessage());
        }
    }

    private void cancleRegistration(OrderLog orderLog) throws Exception {
        try{
            orderLogService.saveLog(orderLog);
        }catch(Exception logException){
            log.error("로그 저장 실패: {}", logException.getMessage());
        }
    }
}
