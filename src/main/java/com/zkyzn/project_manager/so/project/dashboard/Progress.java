package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 进度通用结构
 * @author: Mr-ti
 * Date: 2025/6/13 18:32
 */
//
@Data
public class Progress {
    // 当前进度率（百分比）
    private BigDecimal currentRate;

    // 每日变化率（百分比）
    private BigDecimal dailyChangeRate;
}
