package com.zkyzn.project_manager.constants;

/**
 * 应用常量类
 */
public final class AppConstants {

    // 私有构造函数，防止实例化
    private AppConstants() {
        // 防止工具类被实例化
    }

    /**
     * 系统默认用户ID，用于系统发送消息等场景。
     * 例如，定时报告、系统通知等可以由该ID发送。
     */
    public static final Long SYSTEM_USER_ID = 1L;

}