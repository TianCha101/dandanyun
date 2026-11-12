package com.dandanyun.common;

/**
 * 业务异常。业务上可预期的错误（参数不对、状态不允许）抛这个，
 * 由 GlobalExceptionHandler 统一转成 Result，前端拿到可读的 message。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
