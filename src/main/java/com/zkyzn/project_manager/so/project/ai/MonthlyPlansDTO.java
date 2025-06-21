package com.zkyzn.project_manager.so.project.ai;

import com.zkyzn.project_manager.models.ProjectPlan;
import lombok.Data;

import java.util.List;
/**
 * @author: Mr-ti
 * Date: 2025/6/21 12:39
 */
@Data
public class MonthlyPlansDTO {
    // 本月计划完成情况
    private List<ProjectPlan> currentMonthPlans;
    // 下个月工作计划
    private List<ProjectPlan> nextMonthPlans;
}
