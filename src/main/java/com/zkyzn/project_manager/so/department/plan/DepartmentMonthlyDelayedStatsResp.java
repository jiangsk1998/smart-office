package com.zkyzn.project_manager.so.department.plan;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 科室月度拖期项目统计响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "科室月度拖期项目统计响应体")
public class DepartmentMonthlyDelayedStatsResp {

    @Schema(description = "本月拖期项目数")
    private long thisMonthDelayedCount;

    @Schema(description = "本月拖期数相比上月的变化百分比 (例如: -10.2% / +10.2%)")
    private String changePercentage;

    @Schema(description = "自本月往前十个月的每月拖期项目数（用于柱状图）")
    private List<MonthlyCount> last10MonthsDelayedCounts;

    /**
     * 内部类，用于表示每月的计数值
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "每月计数值")
    public static class MonthlyCount {

        @Schema(description = "月份 (例如: '2025-06')")
        private String month;

        @Schema(description = "当月拖期的项目数量")
        private long count;

        public MonthlyCount(String month, long count) {
            this.month = month;
            this.count = count;
        }
    }
}