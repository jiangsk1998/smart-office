package com.zkyzn.project_manager.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 项目文档类型枚举
 * @author: Mr-ti
 * Date: 2025/6/10 16:03
 */
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentTypeEnum {
    PROJECT_PLAN("项目计划"),
    DRAWING_CATALOG("图纸目录"),
    PRODUCTION_MEETING("生产会材料"),
    REPORT_MATERIAL("汇报材料"),
    SECONDARY_STATISTICS("二次统计"),
    MERGED_DOCUMENT("合并文档"),
    OTHER("其他");

    private final String chineseName;

    DocumentTypeEnum(String chineseName) {
        this.chineseName = chineseName;
    }

    @JsonValue
    public String getChineseName() {
        return chineseName;
    }

    @Override
    public String toString() {
        return chineseName;
    }

    // 可选：根据中文名称获取枚举实例
    public static DocumentTypeEnum fromChineseName(String chineseName) {
        for (DocumentTypeEnum type : values()) {
            if (type.chineseName.equals(chineseName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的文档类型: " + chineseName);
    }
}
