package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Mr-ti
 * Date: 2025/6/13 20:06
 */
@Data
public  class PhaseProgress {
    private String phaseName;
    private BigDecimal plannedProgress;  // 计划进度
    private BigDecimal actualProgress;   // 实际进度
}
