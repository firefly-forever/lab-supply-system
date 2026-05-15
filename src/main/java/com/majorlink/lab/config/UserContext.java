package com.majorlink.lab.config;

/**
 * 用户上下文（ThreadLocal）
 * <p>
 * 在拦截器中将从Token解析的用户信息存入ThreadLocal，
 * 业务层可通过 UserContext.getCurrentUserId() 获取当前操作用户，
 * 避免将userId作为接口参数传入（更安全，防止越权）。
 * 请求结束后拦截器负责清理，防止内存泄漏。
 * </p>
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    // 存入
    public static void set(Long userId, String username, String role) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLE.set(role);
    }

    // 获取当前登录用户ID
    public static Long getCurrentUserId() {
        return USER_ID.get();
    }

    // 获取当前登录用户名
    public static String getCurrentUsername() {
        return USERNAME.get();
    }

    // 获取当前登录用户角色
    public static String getCurrentRole() {
        return ROLE.get();
    }

    // 判断当前用户是否是管理员
    public static boolean isAdmin() {
        return "ADMIN".equals(ROLE.get());
    }

    // 判断当前用户是否是教师或管理员（有审批权限）
    public static boolean isTeacherOrAdmin() {
        String role = ROLE.get();
        return "TEACHER".equals(role) || "ADMIN".equals(role);
    }

    // 请求结束后清理（在拦截器的afterCompletion中调用）
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}
