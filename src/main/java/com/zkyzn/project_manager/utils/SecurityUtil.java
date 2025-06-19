package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.utils.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前用户id 获取当前用户名称
 * @author Jiangsk
 */
public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            // 确保从 CurrentUser 获取的是 Long 类型的 userId
            return currentUser.getAdminId() != null ? currentUser.getAdminId().longValue() : null; // 如果是管理员ID，转换为Long
        }
        // 如果不是CurrentUser类型，或者没有认证信息，返回null或者抛出异常，取决于业务需求
        // 这里返回null，表示无法获取当前用户ID
        return null;
    }

    public static String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            return ((CurrentUser) authentication.getPrincipal()).getUsername();
        }
        return "system"; // 默认返回 "system"
    }
}