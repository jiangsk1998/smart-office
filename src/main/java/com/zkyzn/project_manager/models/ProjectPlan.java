package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
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
@TableName("tab_project_plan")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectPlan {

    @Schema(description = "计划项ID", requiredMode = Schema.RequiredMode.AUTO)
    @TableId(value = "project_plan_id", type = IdType.AUTO)
    private Long projectPlanId;

    @NotNull(message = "关联的阶段ID不能为空") // 从 NotBlank 改为 NotNull，因为可能是Long类型
    @Schema(description = "关联的项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_id")
    private Long projectId;

    @TableField(exist = false) // 标记为非数据库字段，通过关联查询或Service层设置
    private String projectName; // 新增：项目名称，需要确保在查询时填充此字段

    @Schema(description = "关联的阶段ID")
    @TableField("phase_id")
    private Long phaseId;


    @Schema(description = "任务置顶", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("is_top")
    private Boolean isTop = false;

    @NotBlank(message = "任务包不能为空")
    @Schema(description = "任务包", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("task_package")
    private String taskPackage;

    @NotBlank(message = "任务内容不能为空")
    @Schema(description = "任务内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("task_description")
    private String taskDescription;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "责任人不能为空")
    @Schema(description = "责任人", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("responsible_person")
    private String responsiblePerson;

    @NotBlank(message = "科室不能为空")
    @Schema(description = "科室", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("department")
    private String department;

    @Schema(description = "成果", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("deliverable")
    private String deliverable;

    @NotBlank(message = "成果类型不能为空")
    @Schema(description = "成果类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("deliverable_type")
    private String deliverableType;

    @Schema(description = "是否里程碑任务")
    @TableField("is_milestone")
    private Boolean isMilestone = false;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private ZonedDateTime createTime;

    @Schema(description = "修改时间")
    @TableField(value = "update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private ZonedDateTime updateTime;

    @Schema(description = "任务状态（NOT_STARTED/IN_PROGRESS/COMPLETED/STOP）", defaultValue = "NOT_STARTED")
    @TableField(value = "task_status")
    private String taskStatus = TaskStatusEnum.NOT_STARTED.name();

    @Schema(description = "实际开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("real_start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private ZonedDateTime realStartDate;

    @Schema(description = "实际结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("real_end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private ZonedDateTime realEndDate;
}