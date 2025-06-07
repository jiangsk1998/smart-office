package com.zkyzn.project_manager.so.project_info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 项目详情响应体
 */
@Schema(description = "项目详情响应")
@Data
public class ProjectDetailResp {

    /**
     * 项目工号
     */
    @Schema(description = "项目工号")
    private String projectNumber;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 所属科室
     */
    @Schema(description = "所属科室")
    private String department;

    /**
     * 立项时间
     */
    @Schema(description = "立项时间")
    private LocalDate startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDate endDate;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String updateTime;
}