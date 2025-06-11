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

    @Schema(description = "项目状态")
    @TableField("status")
    private String status;

    @Schema(description = "项目当前阶段")
    @TableField("current_phase")
    private String currentPhase;

    @Schema(description = "分管领导ID")
    @TableField("responsible_leader_id")
    private Long responsibleLeaderId;

    @Schema(description = "技术负责人ID")
    @TableField("technical_leader_id")
    private Long technicalLeaderId;

    @Schema(description = "计划主管ID")
    @TableField("plan_supervisor_id")
    private Long planSupervisorId;

    @NotBlank(message = "分管领导不能为空")
    @Schema(description = "分管领导姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("responsible_leader")
    private String responsibleLeader;

    @NotBlank(message = "技术负责人不能为空")
    @Schema(description = "技术负责人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("technical_leader")
    private String technicalLeader;

    @NotBlank(message = "计划主管不能为空，可以有多个人，使用逗号分隔")
    @Schema(description = "计划主管姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("plan_supervisor")
    private String planSupervisor;

    @Schema(description = "项目参与人")
    @TableField("project_participants")
    private String projectParticipants;

    @Schema(description = "是否收藏")
    @TableField("is_favorite")
    private Boolean isFavorite;

    @Schema(description = "创建人ID")
    @TableField("creator_id")
    private Long creatorId;

    @Schema(description = "创建人姓名")
    @TableField("creator_name")
    private String creatorName;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime createTime;

    @Schema(description = "最后修改人ID")
    @TableField("updater_id")
    private Long updaterId;

    @Schema(description = "最后修改人姓名")
    @TableField("updater_name")
    private String updaterName;

    @Schema(description = "修改时间")
    @TableField(value = "update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime updateTime;
}