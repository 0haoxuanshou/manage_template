package com.example.rbacsystem.util;

import com.example.rbacsystem.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户信息
     *
     * @return CustomUserDetails 用户信息
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return Long 用户ID
     */
    public static Long getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return String 用户名
     */
    public static String getCurrentUsername() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return boolean
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}