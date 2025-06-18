package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

/**
 * 历史图纸计划
 */
@Schema(name = "OldDrawingPlan", description = "历史图纸计划")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tab_old_drawing_plan")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OldDrawingPlan {

    @Schema(description = "主键ID")
    @TableId(value = "old_drawing_plan_id", type = IdType.AUTO)
    private Long oldDrawingPlanId;

    @Schema(description = "图纸计划名称")
    @TableField(value = "drawing_plan_name")
    private String drawingPlanName;

    @Schema(description = "图纸计划Hash")
    @TableField(value = "file_hash")
    private String fileHash;

    @Schema(description = "计划开始日期")
    @TableField(value = "plan_start_date")
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期")
    @TableField(value = "plan_end_date")
    private LocalDate planEndDate;
}
