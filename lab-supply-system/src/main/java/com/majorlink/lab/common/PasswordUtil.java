package com.majorlink.lab.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 * <p>
 * 运行 main 方法可生成正确的BCrypt哈希，
 * 将输出结果更新到 db_init.sql 中，或直接执行打印的UPDATE语句。
 * </p>
 */
public class PasswordUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "123456";
        String hash = encoder.encode(raw);

        System.out.println("=== 密码哈希生成工具 ===");
        System.out.println("原始密码: " + raw);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println();
        System.out.println("-- 执行以下SQL更新所有测试用户密码：");
        System.out.println("UPDATE sys_user SET password='" + hash + "' WHERE 1=1;");
        System.out.println();
        System.out.println("-- 验证: " + encoder.matches(raw, hash));
    }
}
