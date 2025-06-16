package com.zkyzn.project_manager.so.personnel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 个人周度未完成事项统计响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "个人周度未完成事项统计响应体")
public class PersonnelWeeklyUncompletedStatsResp {

    @Schema(description = "上周未完成事项数")
    private long lastWeekUncompletedCount;

    @Schema(description = "上周未完成数相比上上周的变化百分比 (例如: -10.2% / +10.2%)")
    private String changePercentage;

    @Schema(description = "本周之前的十周每周未完成事项数（用于柱状图）")
    private List<WeeklyCount> last10WeeksUncompletedCounts;

    /**
     * 内部类，用于表示每周的计数值
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "每周计数值")
    public static class WeeklyCount {

        @Schema(description = "周范围 (例如: '2025-W23')")
        private String weekRange;

        @Schema(description = "当周的未完成事项数量")
        private long count;

        public WeeklyCount(String weekRange, long count) {
            this.weekRange = weekRange;
            this.count = count;
        }
    }
}