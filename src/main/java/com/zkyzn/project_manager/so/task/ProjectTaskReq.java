package com.zkyzn.project_manager.so.task;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author Mr-ti
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectTaskReq {

    @NotNull(message = "关联的项目ID不能为空")
    @Schema(description = "关联的项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("project_id")
    private Long projectId;

    @NotNull(message = "关联的阶段ID不能为空")
    @Schema(description = "关联的阶段ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("phase_id")
    private Long phaseId;

    @NotBlank(message = "任务内容不能为空")
    @Schema(description = "任务内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("task_description")
    private String taskDescription;

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

    @NotBlank(message = "责任人不能为空")
    @Schema(description = "责任人", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("responsible_person")
    private String responsiblePerson;

    @NotBlank(message = "科室不能为空")
    @Schema(description = "科室", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("department")
    private String department;

    @Schema(description = "成果", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("deliverable")
    private String deliverable;

    @Schema(description = "成果类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("deliverable_type")
    private String deliverableType;

    @Schema(description = "是否里程碑任务")
    @JsonProperty("is_milestone")
    private Boolean isMilestone = false;

    @NotNull(message = "任务状态不能为空")
    @Schema(description = "任务状态",
            requiredMode = Schema.RequiredMode.REQUIRED,
            implementation = TaskStatusEnum.class) // 关键修改
    @JsonProperty(value = "task_status")
    private TaskStatusEnum taskStatus;

}
