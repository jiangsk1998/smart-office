package com.zkyzn.project_manager.enums;

import com.fasterxml.jackson.annotation.JsonValue;
/**
 * @author: Mr-ti
 * Date: 2025/6/10 16:22
 */
public enum Operator {
    CREATE("C", "创建"),
    UPDATE("U", "修改"),
    DELETE("D", "删除");

    private final String code;
    private final String description;

    Operator(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue // 确保序列化时使用code值
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 根据编码获取枚举实例
    public static Operator fromCode(String code) {
        for (Operator op : values()) {
            if (op.code.equalsIgnoreCase(code)) {
                return op;
            }
        }
        throw new IllegalArgumentException("无效的操作指令代码: " + code);
    }

    @Override
    public String toString() {
        return code + " - " + description;
    }
}
