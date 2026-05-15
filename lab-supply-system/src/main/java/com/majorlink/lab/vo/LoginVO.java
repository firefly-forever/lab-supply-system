package com.majorlink.lab.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    /** JWT Token，前端后续请求放入 Authorization: Bearer <token> */
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String role;
    private String department;
}
