package com.majorlink.lab.common.exception;

/**
 * 未认证异常（未登录或Token失效）
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException() {
        super("请先登录");
    }
}
