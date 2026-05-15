package com.majorlink.lab.config;

import com.majorlink.lab.entity.SysUser;
import com.majorlink.lab.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动初始化器
 * 自动修复BCrypt密码，并打印测试账号信息。
 * ⚠️ 生产环境部署前请删除或禁用此类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SysUserMapper userMapper;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<SysUser> users = userMapper.selectAll();
            if (users == null || users.isEmpty()) {
                log.warn("用户表为空，请先执行 db_init.sql 初始化数据库");
                return;
            }
            // 生成正确的BCrypt hash并批量更新所有测试用户
            String correctHash = passwordEncoder.encode(DEFAULT_PASSWORD);
            int updated = jdbcTemplate.update("UPDATE sys_user SET password = ?", correctHash);

            log.info("测试账号密码已自动修复（{}个用户），密码均为: {}", updated, DEFAULT_PASSWORD);
            log.info("账号列表: admin(管理员) / teacher1,teacher2(教师) / student1,student2,student3(学生)");
            log.info("接口地址: http://localhost:8080/api");
        } catch (Exception e) {
            log.warn("数据初始化跳过（请检查数据库连接）: {}", e.getMessage());
        }
    }
}
