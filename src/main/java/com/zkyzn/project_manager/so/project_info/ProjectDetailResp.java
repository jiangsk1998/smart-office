package com.zkyzn.project_manager.so.project_info;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/12 17:12
 */
@Data
public class ProjectDetailResp {
    // 项目基本信息
    private ProjectInfo projectInfo;

    // 1. 全周期进度
    private Progress overallProgress;
    // 2. 月进度
    private Progress monthlyProgress;
    // 3. 周进度
    private Progress weeklyProgress;
    // 4. 上周完成项数量
    private Long lastWeekCompletedCount;
    // 5. 项目拖期项
    private RiskItem delayedItems;
    // 6. 项目进度明细
    private List<PhaseProgress> phaseProgressList;
    // 7. 科室月进度
    private List<DepartmentProgress> departmentProgressList;
    // 8. 主要风险项
    private List<RiskItem> riskItems;
    // 9. 回款计划
    private List<PaymentPlan> paymentPlans;
    // 10. 代办事项列表
    private List<UpcomingTask> upcomingTasks;
    // 11. 计划变更清单
    private List<ChangeRecord> changeRecords;
    // 13. 项目的阶段信息列表
    private List<ProjectPhase> phases;

    // 进度通用结构
    @Data
    public static class Progress {
        private BigDecimal currentRate;      // 当前进度百分比
        private BigDecimal dailyChangeRate;   // 较前日变化百分比
        private Long completedCount;         // 已完成数量
        private Long totalCount;             // 总数量
    }

    // 阶段进度明细
    @Data
    public static class PhaseProgress {
        private String phaseName;
        private BigDecimal plannedProgress;  // 计划进度
        private BigDecimal actualProgress;   // 实际进度
    }

    // 科室进度
    @Data
    public static class DepartmentProgress {
        private String department;
        private BigDecimal progressRate;
    }

    // 风险项
    @Data
    public static class RiskItem {
        private String riskName;
        private String riskDetail;
        private Long count; // 仅用于拖期项
    }

    // 回款计划
    @Data
    public static class PaymentPlan {
        private String itemName;
        private BigDecimal plannedAmount;
        private BigDecimal actualAmount;
        private LocalDate plannedDate;
    }

    // 代办事项
    @Data
    public static class UpcomingTask {
        private String taskContent;
        private String responsiblePerson;
        private LocalDate endDate;
        private String priority;
    }

    // 计划变更
    @Data
    public static class ChangeRecord {
        private String taskContent;
        private String changedBy;
        private LocalDate newDate;
    }
}