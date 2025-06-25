package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.enums.PhaseStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * @author: Mr-ti
 * Date: 2025/6/10 17:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_project_phase")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectPhase {

    @TableId(value = "phase_id", type = IdType.AUTO)
    @Schema(description = "阶段唯一ID")
    private Long phaseId;

    @NotNull(message = "关联项目ID不能为空")
    @Schema(description = "关联项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_id")
    private Long projectId;

    @NotBlank(message = "阶段名称不能为空")
    @Schema(description = "阶段名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("phase_name")
    private String phaseName;

    @Schema(description = "阶段状态（NOT_STARTED/IN_PROGRESS/COMPLETED/STOP）",
            defaultValue = "NOT_STARTED")
    @TableField("phase_status")
    private String phaseStatus = PhaseStatusEnum.NOT_STARTED.name();

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

    @NotBlank(message = "负责人不能为空")
    @Schema(description = "负责人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("responsible_person")
    private String responsiblePerson;

    @Schema(description = "科室")
    @TableField("department")
    private String department;

    @Schema(description = "阶段整体成果描述")
    @TableField("deliverable")
    private String deliverable;

    @Schema(description = "成果类型")
    @TableField("deliverable_type")
    private String deliverableType;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime createTime;

    @Schema(description = "修改时间")
    @TableField(value = "update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime updateTime;

    @Schema(description = "顺序码")
    @TableField(value = "sort")
    private Integer sort;
}