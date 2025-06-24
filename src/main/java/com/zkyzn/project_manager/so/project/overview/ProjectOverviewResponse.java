package com.zkyzn.project_manager.so.project.overview;


import lombok.Data;

/**
 * @author: Mr-ti
 * Date: 2025/6/24 17:23
 */
@Data
public class ProjectOverviewResponse {

    /**
     * 进款进度
     */
    private PaymentProgress paymentProgress;

    /**
     * 超期项目
     */
    private OverdueProjects overdueProjects;

    /**
     * 正在执行项目
     */
    private ActiveProjects activeProjects;

    /**
     * 本月项目进度
     */
    private MonthlyProgress monthlyProgress;

    /**
     * 人员项目数据
     */
    private PersonnelData personnelData;
}
