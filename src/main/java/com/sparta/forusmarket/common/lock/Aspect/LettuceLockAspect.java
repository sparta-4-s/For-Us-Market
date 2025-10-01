package com.sparta.forusmarket.common.lock.Aspect;

import com.sparta.forusmarket.common.lock.aop.LettuceLock;
import com.sparta.forusmarket.common.lock.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LettuceLockAspect {
    private static final String LOCK_PREFIX = "LOCK:";
    private final LockService lockService;

    @Around("@annotation(lettuceLock)")
    public Object lettuceLock(ProceedingJoinPoint joinPoint, LettuceLock lettuceLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // key 추출
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            evaluationContext.setVariable(paramNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        String stringKey = parser.parseExpression(lettuceLock.key()).getValue(evaluationContext, String.class);

        if (stringKey == null || stringKey.isBlank()) {
            log.info("Key value is null or blank");
            throw new IllegalArgumentException("Key value is null or blank");
        }

        // lock
        String uniqueId = UUID.randomUUID().toString();
        String LockPrefixKey = LOCK_PREFIX + stringKey;
        int maxRetryCount = 3;

        for (int i = 1; i <= maxRetryCount; i++) {
            boolean available = lockService.tryLock(LockPrefixKey, uniqueId, 4000, 50, 5000);

            // 락 획득 실패
            if (!available) {
                log.info("{} : Retry Count{}", uniqueId, i);
                continue;
            }

            // 락 획득 성공
            log.info("프로세스 락 : {}", uniqueId);
            Object result = joinPoint.proceed();
//
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    // DB 커밋 성공 후 호출
                    public void afterCommit() {
                        lockService.unlock(LockPrefixKey, uniqueId);
                        log.info("프로세스 언락 (트랜잭션 완료) : {}", uniqueId);
                    }
                });
            } else {
                // 트랜잭션이 없는 경우 바로 언락
                lockService.unlock(LockPrefixKey, uniqueId);
                log.info("프로세스 언락 : {}", uniqueId);
            }

            lockService.unlock(LockPrefixKey, uniqueId);
            return result;
        }

        // 재시도 3회 모두 실패 시
        log.info("To many Request");
        throw new IllegalArgumentException("To many Request");
    }
}
