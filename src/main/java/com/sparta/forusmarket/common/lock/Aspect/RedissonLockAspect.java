package com.sparta.forusmarket.common.lock.Aspect;

import com.sparta.forusmarket.common.lock.aop.RedissonLock;
import com.sparta.forusmarket.common.lock.service.RedissonLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
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
public class RedissonLockAspect {
    private static final String LOCK_PREFIX = "LOCK:";
    private final RedissonLockService lockService;

    @Around("@annotation(redissonLock)")
    public Object lettuceLock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // key 추출
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            evaluationContext.setVariable(paramNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        String stringKey = parser.parseExpression(redissonLock.key()).getValue(evaluationContext, String.class);

        if (stringKey == null || stringKey.isBlank()) {
            log.info("Key value is null or blank");
            throw new IllegalArgumentException("Key value is null or blank");
        }

        // lock
        String uniqueId = UUID.randomUUID().toString();
        String lockPrefixKey = LOCK_PREFIX + stringKey;
        RLock lock = null;
        try {
            lock = lockService.tryLock(
                    lockPrefixKey,
                    redissonLock.waitTimeS(),
                    redissonLock.leaseTimeS()
            );

            // 락 획득 실패 (waitTime 초과)
            if (lock == null) {
                throw new IllegalArgumentException("To many Request");
            }

            // 락 획득 성공
            log.info("프로세스 락 : {}", uniqueId);
            Object result = joinPoint.proceed();

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                RLock localLock = lock;
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    // DB 커밋 성공 후 호출
                    @Override
                    public void afterCommit() {
                        lockService.unlock(localLock);
                        log.info("프로세스 언락 (트랜잭션 완료) : {}", uniqueId);
                    }
                });
            } else {
                // 트랜잭션이 없는 경우 바로 언락
                lockService.unlock(lock);
                log.info("프로세스 언락 : {}", uniqueId);
            }
            return result;
        } catch (Throwable e) {
            // 트랜잭션 롤백 시 처리
            lockService.unlock(lock);
            log.info("프로세스 언락 : {}", uniqueId);
            throw e;
        }
    }
}
