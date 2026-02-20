package com.tpanh.server.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP Aspect for automatic logging across all Service layer methods.
 * Logs method execution results and exceptions — no manual log.info() needed in services.
 */
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Pointcut("execution(* com.tpanh.server.modules..service.impl..*(..))")
    public void serviceMethodExecution() {
    }

    @AfterReturning(pointcut = "serviceMethodExecution()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        var className = joinPoint.getTarget().getClass().getSimpleName();
        var methodName = joinPoint.getSignature().getName();
        var args = formatArgs(joinPoint.getArgs());

        if (result != null) {
            log.info("[{}#{}] completed | args: {} | result: {}", className, methodName, args, summarize(result));
        } else {
            log.info("[{}#{}] completed | args: {}", className, methodName, args);
        }
    }

    @AfterThrowing(pointcut = "serviceMethodExecution()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        var className = joinPoint.getTarget().getClass().getSimpleName();
        var methodName = joinPoint.getSignature().getName();
        var args = formatArgs(joinPoint.getArgs());

        log.error("[{}#{}] failed | args: {} | exception: {}", className, methodName, args, ex.getMessage());
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.toString(args);
    }

    private String summarize(Object result) {
        var str = result.toString();
        return str.length() > 200 ? str.substring(0, 200) + "..." : str;
    }
}
