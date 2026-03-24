package com.zhitao.train.common.log.aspect;

import com.zhitao.train.common.log.config.StartupLogger;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    public LogAspect() {
        log.info("LogAspect init...");
    }
    @Pointcut("execution(public * com.zhitao.train.member.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Before("controllerPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();

        log.info("======请求开始======");
        log.info("请求地址:{} {}",request.getRequestURL().toString(),request.getLocalPort());
        log.info("类方法名:{} {}",signature.getDeclaringTypeName(),signature.getName());
        log.info("远程地址:{}",request.getRemoteAddr());

        Object[] args = joinPoint.getArgs();
        ArrayList<Object> target = new ArrayList<>(args.length);
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                continue;
            }
            target.add(arg);
        }
        log.info("请求参数:{}",target.toString());
    }

    @After("controllerPointcut()")
    public void after() {
        log.info("======请求结束======");
    }
}
