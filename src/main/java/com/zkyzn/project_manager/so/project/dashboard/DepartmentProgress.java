package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:18
 */
@Data
public class DepartmentProgress {
    // 科室名称
    private String department;

    // 进度率（百分比）
    private BigDecimal progressRate;
}
