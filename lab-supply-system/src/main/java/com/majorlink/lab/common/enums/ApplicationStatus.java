package com.majorlink.lab.common.enums;

import lombok.Getter;

/**
 * 领用申请状态枚举
 * <p>
 * 状态流转：PENDING -> APPROVED / REJECTED
 * </p>
 */
@Getter
public enum ApplicationStatus {
    PENDING("待审批"),
    APPROVED("已通过"),
    REJECTED("已拒绝");

    private final String desc;

    ApplicationStatus(String desc) {
        this.desc = desc;
    }
}
