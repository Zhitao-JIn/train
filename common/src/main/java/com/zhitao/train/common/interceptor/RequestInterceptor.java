package com.zhitao.train.common.interceptor;

import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器
 */
@Component
public class RequestInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 增加日志流水号
        MDC.put("LOG_ID", request.getHeader("LOG_ID"));
        LOG.info("获取线程ID:{}",request.getHeader("LOG_ID"));
        return true;
    }

}
