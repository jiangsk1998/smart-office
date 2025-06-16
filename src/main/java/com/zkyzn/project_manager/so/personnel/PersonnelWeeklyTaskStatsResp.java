package com.zkyzn.project_manager.so.personnel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 个人任务每周统计响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "个人任务每周统计响应")
public class PersonnelWeeklyTaskStatsResp {

    @Schema(description = "本周到期任务数")
    private long thisWeekDueCount;

    @Schema(description = "本周到期数与上周到期数的变化百分比 (例如: -10.2% / +10.2%)")
    private String changePercentage;

    @Schema(description = "近10周的每周到期任务数 (用于柱状图)")
    private List<WeeklyCount> last10WeeksDueCounts;

    /**
     * 内部类，用于表示每周的计数值
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "每周计数值")
    public static class WeeklyCount {

        @Schema(description = "周范围 (例如: '2025-W23')")
        private String weekRange;

        @Schema(description = "当周到期的任务数量")
        private long count;

        public WeeklyCount(String weekRange, long count) {
            this.weekRange = weekRange;
            this.count = count;
        }
    }
}