package com.zhitao.train.common.log.aspect;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Aspect
@Component
public class RequestLogAspect {
    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);

    public RequestLogAspect() {
        log.info("RequestLogAspect init...");
    }

    @Pointcut("execution(public * com.zhitao.train..controller..*.*(..))")
    public void controllerPointcut() {}

    // ⭐⭐⭐ 只用 Around 统一处理：正常返回 + 异常抛出 ⭐⭐⭐
    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();

        log.info("======请求开始======");
        log.info("请求地址:{} {}", request.getRequestURL().toString(), request.getLocalPort());
        log.info("类方法名:{} {}", signature.getDeclaringTypeName(), signature.getName());
        log.info("远程地址:{}", request.getRemoteAddr());

        Object[] args = joinPoint.getArgs();
        ArrayList<Object> target = new ArrayList<>(args.length);
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                continue;
            }
            target.add(arg);
        }

        try {
            // 执行业务方法
            Object result = joinPoint.proceed();
            log.info("======请求结束======");
            return result;

        } catch (Throwable e) {
            // ⭐⭐⭐ 这里捕获异常 → 打印 → 必须继续抛出去 ⭐⭐⭐
            log.error("{} 异常：{}", joinPoint.getSignature().toShortString(), e.getMessage(), e);
            log.info("======请求结束======");

            // 这一行才是让全局异常生效的关键！
            throw e;
        }
    }
}