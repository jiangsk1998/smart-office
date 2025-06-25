package com.zkyzn.project_manager.so.project.overview;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Mr-ti
 * Date: 2025/6/25 10:37
 */
@Data
public class DepartmentMonthlyProgress {

    /**
     * 科室名称
     */
    private String department;

    /**
     * 完成百分比
     */
    private BigDecimal completionRate;

    /**
     * 总任务数
     */
    private int totalTasks;

    /**
     * 已完成任务数
     */
    private int completedTasks;
}
