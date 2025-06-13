package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:15
 */
@Data
public class PaymentPlan {
    // 款项名称
    private String itemName;

    // 计划金额
    private BigDecimal plannedAmount;

    // 实际金额
    private BigDecimal actualAmount;

    // 计划日期
    private LocalDate plannedDate;
}
