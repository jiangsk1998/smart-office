package com.zkyzn.project_manager.so.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 科室任务每日统计响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "科室任务每日统计响应")
public class DepartmentTaskStatsResp {

    @Schema(description = "今日到期任务数")
    private long todayDueCount;

    @Schema(description = "今日到期数与昨日到期数的变化百分比 (例如: -10.2% / +10.2%)")
    private String changePercentage;

    @Schema(description = "近10日的每日到期任务数 (用于柱状图)")
    private List<DailyCount> last10DaysDueCounts;

    /**
     * 内部类，用于表示每日的计数值
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "每日计数值")
    public static class DailyCount {

        @Schema(description = "日期 (格式:yyyy-MM-dd)")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate date;

        @Schema(description = "当天到期的任务数量")
        private long count;

        public DailyCount(LocalDate date, long count) {
            this.date = date;
            this.count = count;
        }
    }
}