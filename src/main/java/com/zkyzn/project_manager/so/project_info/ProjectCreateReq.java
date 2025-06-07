package com.zkyzn.project_manager.so.project_info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建项目请求体
 */
@Schema(description = "创建项目请求")
@Data
public class ProjectCreateReq {

    /**
     * 项目工号
     */
    @Schema(description = "项目工号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectName;

    /**
     * 所属科室
     */
    @Schema(description = "所属科室", requiredMode = Schema.RequiredMode.REQUIRED)
    private String department;

    /**
     * 立项时间
     */
    @Schema(description = "立项时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    /**
     * 分管领导
     */
    @Schema(description = "分管领导ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long responsibleLeaderID;

    /**
     * 技术负责人
     */
    @Schema(description = "技术负责人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long technicalLeaderId;

    /**
     * 计划主管
     */
    @Schema(description = "计划主管ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long planSupervisorId;

    /**
     * 分管领导
     */
    @Schema(description = "分管领导姓名")
    private String responsibleLeader;

    /**
     * 技术负责人
     */
    @Schema(description = "技术负责人姓名")
    private String technicalLeader;

    /**
     * 计划主管
     */
    @Schema(description = "计划主管姓名")
    private String planSupervisor;


    /**
     * 项目状态
     */
    @Schema(description = "项目状态")
    private String status;

    /**
     * 项目当前阶段
     */
    @Schema(description = "项目当前阶段")
    private String currentPhase;

    /**
     * 创建项目用户Id
     */
    @Schema(description = "创建项目用户Id")
    private Long creatorId;

    /**
     * 合同文档地址
     */
    @Schema(description = "合同文档地址")
    private String contractDocumentUrl;

    /**
     * 合同文档名称
     */
    @Schema(description = "合同文档名称")
    private String contractDocumentName;

    /**
     * 需求文档地址
     */
    @Schema(description = "需求文档地址")
    private String requirementDocumentUrl;

    /**
     * 需求文档名称
     */
    @Schema(description = "需求文档名称")
    private String requirementDocumentName;

    /**
     * 策划书地址
     */
    @Schema(description = "策划书地址")
    private String proposalUrl;

    /**
     * 策划书名称
     */
    @Schema(description = "策划书名称")
    private String proposalName;

    /**
     * 计划书地址
     */
    @Schema(description = "计划书地址")
    private String planBookUrl;

    /**
     * 计划书名称
     */
    @Schema(description = "计划书名称")
    private String planBookName;

    /**
     * 图纸计划地址
     */
    @Schema(description = "图纸计划地址")
    private String drawingPlanUrl;

    /**
     * 图纸计划名称
     */
    @Schema(description = "图纸计划名称")
    private String drawingPlanName;
}