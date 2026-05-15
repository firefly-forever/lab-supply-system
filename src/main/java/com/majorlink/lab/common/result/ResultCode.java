package com.majorlink.lab.common.result;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 通用失败
    FAIL(400, "操作失败"),
    UNAUTHORIZED(401, "请先登录"),
    FORBIDDEN(403, "无权限执行此操作"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    // 业务错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务特定错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "账号已被禁用"),
    PASSWORD_ERROR(1003, "用户名或密码错误"),
    TOKEN_EXPIRED(1004, "登录已过期，请重新登录"),
    TOKEN_INVALID(1005, "无效的Token"),

    SUPPLY_NOT_FOUND(2001, "耗材不存在"),
    SUPPLY_STOCK_NOT_ENOUGH(2002, "库存不足，无法出库"),
    SUPPLY_CODE_DUPLICATE(2003, "耗材编号已存在"),

    APPLICATION_NOT_FOUND(3001, "申请记录不存在"),
    APPLICATION_ALREADY_APPROVED(3002, "申请已审批，不可重复操作"),
    APPLICATION_NOT_PENDING(3003, "只能审批待审批状态的申请");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
