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
/**
 * Copyright(C) 2024 HFHX.All right reserved.
 * ClassName: DrawingPlan
 * Description: TODO
 * Version: 1.0
 * Author: Mr-ti
 * Date: 2025/6/7 21:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_drawing_plan")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DrawingPlan {

    @Schema(description = "图纸唯一ID", requiredMode = Schema.RequiredMode.AUTO)
    @TableId(value = "drawing_plan_id", type = IdType.AUTO)
    private Long drawingPlanId;

    @NotBlank(message = "关联项目ID不能为空")
    @Schema(description = "关联项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_id")
    private String projectId;

    @NotBlank(message = "图号不能为空")
    @Schema(description = "图号", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("drawing_number")
    private String drawingNumber;

    @NotBlank(message = "图纸名称不能为空")
    @Schema(description = "图纸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("drawing_name")
    private String drawingName;

    @NotBlank(message = "审签流程不能为空")
    @Schema(description = "审签流程", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("approval_flow")
    private String approvalFlow;

    @NotNull(message = "完成时间不能为空")
    @Schema(description = "完成时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("completion_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completionDate;

    @NotBlank(message = "部门不能为空")
    @Schema(description = "部门", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("department")
    private String department;

    @NotBlank(message = "密级不能为空")
    @Schema(description = "密级", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("security_level")
    private String securityLevel;
}
