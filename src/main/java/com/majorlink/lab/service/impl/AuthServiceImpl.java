package com.majorlink.lab.service.impl;

import com.majorlink.lab.common.exception.BusinessException;
import com.majorlink.lab.common.result.ResultCode;
import com.majorlink.lab.config.JwtUtil;
import com.majorlink.lab.dto.LoginDTO;
import com.majorlink.lab.entity.SysUser;
import com.majorlink.lab.mapper.SysUserMapper;
import com.majorlink.lab.service.AuthService;
import com.majorlink.lab.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        SysUser user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            // 不区分"用户不存在"和"密码错误"，防止用户名枚举攻击
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 2. 校验账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 3. 校验密码（BCrypt验证）
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("用户 [{}] 密码错误", loginDTO.getUsername());
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 5. 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setRole(user.getRole());
        loginVO.setDepartment(user.getDepartment());

        log.info("用户 [{}] ({}) 登录成功", user.getUsername(), user.getRole());
        return loginVO;
    }
}
