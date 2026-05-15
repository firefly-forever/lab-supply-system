package com.majorlink.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
public class SysUser {
    private Long id;
    /** 登录用户名 */
    private String username;
    /** 密码（BCrypt加密） */
    private String password;
    /** 真实姓名 */
    private String realName;
    /** 角色：STUDENT/TEACHER/ADMIN */
    private String role;
    /** 所属院系/科室 */
    private String department;
    private String phone;
    private String email;
    /** 状态：1启用 0禁用 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
