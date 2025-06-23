package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.utils.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // 引入 UserDetails

/**
 * 获取当前用户id 获取当前用户名称
 * @author Jiangsk
 */
public class SecurityUtil {

    /**
     * 获取当前认证用户的 UserDetails 对象。
     * 这是一个通用的方法，用于获取 Spring Security 上下文中的用户主体。
     * @return 当前认证用户的 UserDetails 对象，如果未认证或不是预期的 CurrentUser 类型则返回 null。
     */
    public static UserDetails getCurrentUser() { //
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) { //
            return (UserDetails) authentication.getPrincipal(); //
        }
        return null; //
    }

    public static Long getCurrentUserId() {
        UserDetails userDetails = getCurrentUser(); //
        if (userDetails instanceof CurrentUser currentUser) { //
            // 确保从 CurrentUser 获取的是 Long 类型的 userId
            return currentUser.getAdminId() != null ? currentUser.getAdminId().longValue() : null; //
        }
        // 如果不是CurrentUser类型，或者没有认证信息，返回null或者抛出异常，取决于业务需求
        return 1L; //
    }

    public static String getCurrentUserName() {
        UserDetails userDetails = getCurrentUser(); //
        if (userDetails instanceof CurrentUser currentUser) { //
            return currentUser.getUsername(); //
        }
        return "system"; // 默认返回 "system"
    }
}