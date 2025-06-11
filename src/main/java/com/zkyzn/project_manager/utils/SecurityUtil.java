package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.utils.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
    TODO  获取当前用户id 获取当前用户名称
 * @author Jiangsk
 */
public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            Integer adminId = currentUser.getAdminId();
            return adminId != null ? adminId.longValue() : 1L;
        }
        return 1L;
    }

    public static String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            return ((CurrentUser) authentication.getPrincipal()).getUsername();
        }
        return "system";
    }
}