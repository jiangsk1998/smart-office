package com.zkyzn.project_manager.enums;


public enum ProjectStatusEnum {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    ON_HOLD("已暂停"),
    COMPLETED("已完结"),
    CANCELLED("已取消");

    private final String displayName;

    ProjectStatusEnum(String displayName) {
        this.displayName = displayName;
    }
}
