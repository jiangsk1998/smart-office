package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * @author Mr-ti
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_project_info")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectInfo {

    @TableId(value = "project_id", type = IdType.AUTO)
    @Schema(description = "项目主键ID")
    private Long projectId;

    @NotBlank(message = "项目工号不能为空")
    @Schema(description = "项目工号", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_number")
    private String projectNumber;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_name")
    private String projectName;

    @NotNull(message = "所属科室不能为空")
    @Schema(description = "所属科室", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("department")
    private String department;

    @NotNull(message = "立项时间不能为空")
    @Schema(description = "立项时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "项目状态不能为空")
    @Schema(description = "项目状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("status")
    private String status;

    @NotBlank(message = "项目当前阶段不能为空")
    @Schema(description = "项目当前阶段", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("current_phase")
    private String currentPhase;

    @NotNull(message = "分管领导ID不能为空")
    @Schema(description = "分管领导ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("responsible_leader_id")
    private Long responsibleLeaderId;

    @NotNull(message = "技术负责人ID不能为空")
    @Schema(description = "技术负责人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("technical_leader_id")
    private Long technicalLeaderId;

    @NotNull(message = "计划主管ID不能为空")
    @Schema(description = "计划主管ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("plan_supervisor_id")
    private Long planSupervisorId;

    @Schema(description = "分管领导姓名")
    @TableField("responsible_leader")
    private String responsibleLeader;

    @Schema(description = "技术负责人姓名")
    @TableField("technical_leader")
    private String technicalLeader;

    @Schema(description = "计划主管姓名")
    @TableField("plan_supervisor")
    private String planSupervisor;

    @NotNull(message = "创建人ID不能为空")
    @Schema(description = "创建人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("creator_id")
    private Long creatorId;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime createTime;

    @Schema(description = "最后修改人ID")
    @TableField("updater_id")
    private Long updaterId;

    @Schema(description = "修改时间")
    @TableField(value = "update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime updateTime;
}