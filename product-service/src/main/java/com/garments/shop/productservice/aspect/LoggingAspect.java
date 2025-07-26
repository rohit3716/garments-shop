package com.garments.shop.productservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.garments.shop.productservice.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("→ Executing {}.{} with arguments: {}", className, methodName, Arrays.toString(args));
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("← Completed {}.{} in {}ms with result: {}", 
                    className, methodName, (endTime - startTime), result);
            return result;
        } catch (Exception e) {
            log.error("× Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    @Around("@annotation(com.garments.shop.productservice.aspect.TrackExecutionTime)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("⏱ Method {}.{} executed in {}ms", className, methodName, (endTime - startTime));
        return result;
    }
}
