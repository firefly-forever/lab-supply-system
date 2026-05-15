package com.majorlink.lab.service;

import com.majorlink.lab.dto.LoginDTO;
import com.majorlink.lab.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录
     *
     * @param loginDTO 登录请求（用户名 + 密码）
     * @return 登录成功响应（包含Token和用户信息）
     */
    LoginVO login(LoginDTO loginDTO);
}
