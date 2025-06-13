package com.zkyzn.project_manager.so.department;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 人员月度工作进度响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "人员月度工作进度响应体")
public class PersonnelMonthlyProgressResp {

    @Schema(description = "责任人姓名")
    private String responsiblePerson;

    @Schema(description = "本月应完成任务总数")
    private long totalTasks;

    @Schema(description = "本月已完成任务数")
    private long completedTasks;

    @Schema(description = "月度完成进度（百分比）")
    private BigDecimal monthlyProgress;

    @Schema(description = "月度完成进度（分数形式）")
    private String monthlyProgressFraction; // 新增字段
}