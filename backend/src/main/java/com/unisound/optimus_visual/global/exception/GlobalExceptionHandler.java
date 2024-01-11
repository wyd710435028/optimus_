package com.unisound.optimus_visual.global.exception;

import com.unisound.optimus_visual.global.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理全局配置类
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(CommonException.class)
    @ResponseBody
    public CommonResult handleStudentException(HttpServletRequest request, CommonException ex) {
        CommonResult result;
        log.error("Exception code:{},msg:{}",ex.getResponse().getCode(),ex.getResponse().getMsg());
        result = new CommonResult(ex.getResponse().getCode(),ex.getResponse().getMsg());
        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult handleException(HttpServletRequest request, Exception ex) {
        CommonResult result;
        log.error("exception error:{}",ex);
        result = new CommonResult(ErrorCodeAndMsg.Network_error.getCode(),
                ErrorCodeAndMsg.Network_error.getMsg());
        return result;
    }
}
