package com.zhitao.train.common.exception;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhitao.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /*
    @ExceptionHandler(RuntimeException.class)
    public CommonResp handleException(RuntimeException e){
        if(e instanceof TestException){
            return CommonResp.builder().message(e.getMessage()+"请联系管理员").success(false).build();
        }else {
            return CommonResp.builder().message(e.getMessage()+"system error").success(false).build();
        }
    }*/
    @ExceptionHandler(Exception.class)
    public CommonResp<Object> handleException(Exception e) throws Exception {
        LOG.error("捕获异常:"+e.getMessage());
        if(StrUtil.isNotBlank(RootContext.getXID())){
            throw e;
        }
        return new CommonResp<>(false,"请联系管理员:"+e.getMessage());
    }
    /*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResp handleException(MethodArgumentNotValidException e){
        return CommonResp.builder().message(e.getMessage()).success(false).build();
    }

     */
}
