package com.zhitao.train.gateway.filter;

import cn.hutool.core.util.RandomUtil;
import com.zhitao.train.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestFilter implements Ordered, GlobalFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String logId =
                System.currentTimeMillis()
                        + RandomUtil.randomString(3);

        // 放入 MDC（日志用）
        MDC.put("LOG_ID", logId);
        LOG.info("设置线程ID:{}",logId);
        // 写入 Header（传给微服务）
        ServerWebExchange mutatedExchange =
                exchange.mutate()
                        .request(
                                exchange.getRequest()
                                        .mutate()
                                        .header("LOG_ID", logId)
                                        .build()
                        )
                        .build();

        return chain.filter(mutatedExchange)
                .doFinally(signal -> MDC.clear());
    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }
}