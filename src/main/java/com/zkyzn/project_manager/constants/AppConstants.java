package com.zkyzn.project_manager.constants;

import java.util.List;

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

    /**
     * 文件资源目录
     */
    public static final List<String> FOLDER_TYPES = List.of("项目计划", "图纸目录", "生产会材料", "汇报材料", "二次统计", "合并文档", "项目合同", "其他");

}