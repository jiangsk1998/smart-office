package com.zkyzn.project_manager.enums;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "项目状态枚举")
public enum ProjectStatusEnum {
    @Schema(description = "未开始")
    NOT_STARTED("未开始"),

    @Schema(description = "进行中")
    IN_PROGRESS("进行中"),

    @Schema(description = "超期")
    OVERDUE("超期"),

    @Schema(description = "已完成")
    COMPLETED("已完成");

    private final String displayName;

    ProjectStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
