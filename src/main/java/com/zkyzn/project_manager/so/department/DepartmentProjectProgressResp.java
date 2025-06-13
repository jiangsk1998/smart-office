package com.zkyzn.project_manager.so.department;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "科室项目月进度响应")
public class DepartmentProjectProgressResp {

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目工号")
    private String projectNumber;

    @Schema(description = "分管领导")
    private String responsibleLeader;

    @Schema(description = "月进度（百分比）")
    private BigDecimal monthlyProgress;
    
    @Schema(description = "月进度（分数形式）")
    private String monthlyProgressFraction;
}