package com.zkyzn.project_manager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 到款状态枚举
 * @author: Mr-ti
 * Date: 2025/6/21 00:00
 */
@Getter
public enum PaymentStatusEnum {
    PENDING("PENDING", "待处理"),
    WARNING("WARNING", "预警"),
    PROCESSING("PROCESSING", "处理中"),
    COMPLETED("COMPLETED", "已完成");

    @EnumValue
    private final String code;
    private final String description;

    PaymentStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
