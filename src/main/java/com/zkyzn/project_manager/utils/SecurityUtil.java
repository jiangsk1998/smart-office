package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.utils.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for accessing security context information.
 */
public class SecurityUtil {

    /**
     * Retrieves the current user's ID from the security context.
     * @return The current user's ID, or a default value (1L) if not found.
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            Integer adminId = currentUser.getAdminId();
            return adminId != null ? adminId.longValue() : 1L; // Default to 1L if null
        }
        // Fallback for unauthenticated operations or tests, as seen in controllers
        return 1L;
    }

    /**
     * Retrieves the current user's name from the security context.
     * @return The current user's name, or a default value ("system") if not found.
     */
    public static String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            return ((CurrentUser) authentication.getPrincipal()).getUsername();
        }
        // Fallback for unauthenticated operations
        return "system";
    }
}