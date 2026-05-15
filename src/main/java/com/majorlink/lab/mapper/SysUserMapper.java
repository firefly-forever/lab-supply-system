package com.majorlink.lab.mapper;

import com.majorlink.lab.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface SysUserMapper {

    /** 根据用户名查询（用于登录验证） */
    SysUser selectByUsername(@Param("username") String username);

    /** 根据ID查询 */
    SysUser selectById(@Param("id") Long id);

    /** 查询所有用户（管理员用） */
    List<SysUser> selectAll();

    /** 按角色查询用户列表（如查询所有教师） */
    List<SysUser> selectByRole(@Param("role") String role);

    /** 新增用户 */
    int insert(SysUser user);

    /** 更新用户信息 */
    int update(SysUser user);

    /** 修改用户状态（启用/禁用） */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
