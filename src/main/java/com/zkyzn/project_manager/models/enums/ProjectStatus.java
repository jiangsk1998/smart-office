package com.zkyzn.project_manager.models.enums;


/**
 * Copyright(C) 2024 HFHX.All right reserved.
 * ClassName: ProjectStatus
 * Description: TODO
 * Version: 1.0
 * Author: Mr-ti
 * Date: 2025/6/7 13:51
 */
public enum ProjectStatus {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    ON_HOLD("已暂停"),
    COMPLETED("已完结"),
    CANCELLED("已取消");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }
}
