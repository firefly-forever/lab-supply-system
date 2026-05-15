package com.majorlink.lab.controller;

import com.majorlink.lab.common.exception.BusinessException;
import com.majorlink.lab.common.result.Result;
import com.majorlink.lab.config.UserContext;
import com.majorlink.lab.entity.SysUser;
import com.majorlink.lab.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理接口（仅管理员使用）
 *
 * GET  /api/user/list         查询所有用户
 * GET  /api/user/teachers     查询所有教师（审批人下拉列表）
 * GET  /api/user/me           查询当前登录用户信息
 * PUT  /api/user/{id}/status  启用/禁用用户
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper userMapper;

    /** 查询所有用户（管理员） */
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "仅管理员可查看用户列表");
        }
        return Result.success(userMapper.selectAll());
    }

    /** 查询所有教师列表（学生提交申请时，前端可展示审批人列表供参考） */
    @GetMapping("/teachers")
    public Result<List<SysUser>> getTeachers() {
        return Result.success(userMapper.selectByRole("TEACHER"));
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/me")
    public Result<SysUser> getCurrentUser() {
        SysUser user = userMapper.selectById(UserContext.getCurrentUserId());
        if (user == null) {
            throw new BusinessException("用户信息不存在");
        }
        // 清空密码字段，不返回给前端
        user.setPassword(null);
        return Result.success(user);
    }

    /** 修改用户状态（管理员启用/禁用账号） */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "仅管理员可修改用户状态");
        }
        userMapper.updateStatus(id, status);
        return Result.success(status == 1 ? "用户已启用" : "用户已禁用");
    }
}
