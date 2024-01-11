package com.unisound.optimus_visual.global.exception;

/**
 * 统一异常捕获
 */
public class CommonException extends RuntimeException{
    private static final long serialVersionUID = -6370612186038915645L;

    private final ErrorCodeAndMsg result;

    public CommonException(ErrorCodeAndMsg result) {
        this.result = result;
    }
    public ErrorCodeAndMsg getResponse() {
        return result;
    }
}
