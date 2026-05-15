package com.majorlink.lab.common.exception;

import com.majorlink.lab.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于在 Service 层抛出可预期的业务错误（如库存不足、权限不够等），
 * 由全局异常处理器捕获并转换为统一响应格式。
 * 不代表程序 Bug，不需要打印完整堆栈。
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
