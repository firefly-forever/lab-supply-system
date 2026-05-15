package com.majorlink.lab.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 * <p>
 * 所有接口均通过此类返回，前端根据 code 判断业务是否成功：
 * - code=200：成功
 * - code=4xx：客户端错误（参数错误、权限不足等）
 * - code=5xx：服务端错误
 * </p>
 *
 * @param <T> 响应数据类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    /** 响应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据（失败时为null） */
    private T data;

    // ==================== 私有构造，通过静态工厂方法创建 ====================

    private Result() {}

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    /** 成功，无数据 */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /** 成功，自定义消息 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 失败响应 ====================

    /** 失败，自定义消息 */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.FAIL.getCode(), message, null);
    }

    /** 失败，使用枚举 */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /** 失败，自定义code和消息 */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /** 未登录 */
    public static <T> Result<T> unauthorized() {
        return fail(ResultCode.UNAUTHORIZED);
    }

    /** 无权限 */
    public static <T> Result<T> forbidden() {
        return fail(ResultCode.FORBIDDEN);
    }

    /** 判断是否成功 */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }

    /** 成功，只有消息，无数据 */
    public static Result<String> success(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, null);
    }
}
