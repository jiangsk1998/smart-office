package com.zkyzn.project_manager.so.personnel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 个人待办事项响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "个人待办事项响应体")
public class PersonnelTodoTaskResp {

    // --- 项目信息 ---
    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目工号")
    private String projectNumber;
    
    // --- 任务（计划）信息 ---
    @Schema(description = "计划项ID")
    private Long projectPlanId;

    @Schema(description = "任务包（所属阶段）")
    private String taskPackage;

    @Schema(description = "任务内容")
    private String taskDescription;

    @Schema(description = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "责任人")
    private String responsiblePerson;

    @Schema(description = "成果")
    private String deliverable;

    @Schema(description = "任务状态")
    private String taskStatus;
    
    @Schema(description = "是否里程碑任务")
    private Boolean isMilestone;

    @Schema(description = "是否置顶")
    private Boolean isTop;
}