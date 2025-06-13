package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;
/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:13
 */
@Data
public class ProgressOverview {
    // 全周期进度
    private Progress overallProgress;

    // 月进度
    private Progress monthlyProgress;

    // 周进度
    private Progress weeklyProgress;

    // 上周完成项数量
    private long lastWeekCompletedCount;

    // 项目拖期项
    private long delayedCompletedCount;
}
