package com.zkyzn.project_manager.so.phase;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.enums.PhaseStatusEnum;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author: Mr-ti
 * Date: 2025/6/10 17:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectPhaseReq {

    @NotNull(message = "关联项目ID不能为空")
    @Schema(description = "关联项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "project_id",required = true)
    private Long projectId;

    @NotNull(message = "科室名称不能为空")
    @Schema(description = "关联科室", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "department",required = true)
    private String department;

    @NotBlank(message = "阶段名称不能为空")
    @Schema(description = "阶段名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "phase_name",required = true)
    private String phaseName;

    @Schema(description = "阶段状态（未开始/进行中/已完成/中止）", implementation = PhaseStatusEnum.class)
    @JsonProperty("phase_status")
    private PhaseStatusEnum phaseStatus = PhaseStatusEnum.NOT_STARTED;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "负责人不能为空")
    @Schema(description = "负责人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("responsible_person")
    private String responsiblePerson;

    @Schema(description = "阶段整体成果描述")
    @JsonProperty("deliverable")
    private String deliverable;

    @Schema(description = "成果类型")
    @JsonProperty("deliverable_type")
    private String deliverableType;

}