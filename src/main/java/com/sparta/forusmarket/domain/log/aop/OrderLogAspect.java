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

    /**
     * 주문 서비스의 메서드 실행 전/후를 감싸서
     * 주문 성공, 실패, 취소 등의 로그를 DB에 기록하는 AOP 클래스.
     */
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

            if (result instanceof OrderResponse resp) {
                if (resp.getStatus() == OrderStatus.SUCCESS) {
                    // 주문 성공시 성공 로그 저장
                    orderLogService.saveLog(OrderLog.success(
                            resp.getId(), resp.getUserId(), resp.getProductId(),
                            resp.getQuantity(), resp.getPrice()
                    ));
                } else {
                    // 주문 실패(FAIL 응답) 시 실패 로그 저장
                    orderLogService.saveLog(OrderLog.fail("Order failed"));
                }
            }
            return result;
        } catch (Throwable ex) {
            log.error("[AUDIT-ERROR] ex={}", ex.toString(), ex);
            try { orderLogService.saveLog(OrderLog.fail(String.valueOf(ex.getMessage()))); }
            catch (Exception logEx) { log.error("[AUDIT-ERROR] 로그 저장 실패", logEx); }
            throw ex;
        }

    }

    /**
     * 주문 취소 시 AOP가 실행되어 취소 로그를 저장한다.
     *
     * @param pjp join point (실제 cancelOrder 메서드 호출 정보)
     * @return cancelOrder 메서드의 원래 반환값(OrderResponse)
     * @throws Throwable 원래 메서드가 던지는 예외 그대로 전파
     */
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
                // 주문 취소 성공 시 취소 로그 저장
                OrderLog cancel = OrderLog.cancel(orderId, userId, productId, quantity, price, message);
                cancelRegistration(cancel);
            }
            return result;
        } catch (Throwable ex) {
            log.error("[AUDIT-ERROR] op={}, ex={}", ex.toString(), ex);
            OrderLog fail = OrderLog.fail(ex.getMessage().toString());
            failRegistration(fail);
            throw ex;
        }
    }

    /** 주문 실패 로그 저장 (실패 시 메인 트랜잭션에 영향 없음) */
    private void failRegistration(OrderLog orderLog) throws Exception {
        try {
            orderLogService.saveLog(orderLog);
        } catch (Exception logException) {
            log.error("로그 저장 실패: {}", logException.getMessage());
        }
    }

    /** 주문 취소 로그 저장 (실패 시 메인 트랜잭션에 영향 없음) */
    private void cancelRegistration(OrderLog orderLog) throws Exception {
        try{
            orderLogService.saveLog(orderLog);
        }catch(Exception logException){
            log.error("로그 저장 실패: {}", logException.getMessage());
        }
    }
}
