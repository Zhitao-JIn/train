package com.zhitao.train.business.config;

import com.zhitao.train.common.interceptor.RequestInterceptor;
import com.zhitao.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

   @Resource
   RequestInterceptor requstInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(requstInterceptor)
              .addPathPatterns("/**");

      registry.addInterceptor(memberInterceptor)
              .addPathPatterns("/**")
              .excludePathPatterns(
                      "/business/hello"
              );
   }
}
