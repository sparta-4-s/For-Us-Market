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

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LettuceLockAspect {
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
            log.info("Key is blank");
            return joinPoint.proceed();
        }

        long key;
        try {
            key = Long.parseLong(stringKey);
        } catch (NumberFormatException e) {
            log.info("Key type must be Long : {}", stringKey);
            return joinPoint.proceed();
        }

        lockService.lock(key);
        Object result = joinPoint.proceed();
        lockService.unlock(key);

        return result;
    }
}
