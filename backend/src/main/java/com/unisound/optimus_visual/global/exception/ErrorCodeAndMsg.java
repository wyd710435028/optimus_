package com.unisound.optimus_visual.global.exception;

/**
 * 异常枚举
 */
public enum ErrorCodeAndMsg {

    Network_error("9999","网络错误，待会重试"),
    Null_error("88888","查询结果为空")
    ;

    private String code;
    private String msg;

    ErrorCodeAndMsg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
