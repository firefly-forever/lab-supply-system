package com.majorlink.lab.controller;

import com.majorlink.lab.common.result.Result;
import com.majorlink.lab.dto.LoginDTO;
import com.majorlink.lab.service.AuthService;
import com.majorlink.lab.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 * <p>
 * 注意：此Controller下的路径已在 WebMvcConfig 白名单中，无需Token
 * </p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     * POST /api/auth/login
     *
     * @param loginDTO 登录请求体 { "username": "admin", "password": "123456" }
     * @return 包含JWT Token和用户信息的响应
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }
}
