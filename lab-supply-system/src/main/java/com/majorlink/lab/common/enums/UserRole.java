package com.majorlink.lab.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /** 学生：只能提交领用申请 */
    STUDENT,
    /** 教师：可审批学生申请，可查看本实验室库存 */
    TEACHER,
    /** 管理员：拥有所有权限，含入库、直接出库、用户管理 */
    ADMIN
}
