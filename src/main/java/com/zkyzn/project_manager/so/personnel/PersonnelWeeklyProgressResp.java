package com.zkyzn.project_manager.so.personnel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 个人周工作完成进度响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "个人周工作完成进度响应体")
public class PersonnelWeeklyProgressResp {

    @Schema(description = "上周工作完成进度（百分比）")
    private BigDecimal lastWeekProgress;

    @Schema(description = "上周进度与上上周进度的变化百分比 (例如: -10.2% / +10.2%)")
    private String changePercentage;

    @Schema(description = "本周之前的十周工作进度（用于柱状图）")
    private List<WeeklyProgress> last10WeeksProgress;

    /**
     * 内部类，用于表示每周的进度
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "每周进度")
    public static class WeeklyProgress {

        @Schema(description = "周范围 (例如: '2025-W23')")
        private String weekRange;

        @Schema(description = "当周的完成进度（百分比）")
        private BigDecimal progress;

        public WeeklyProgress(String weekRange, BigDecimal progress) {
            this.weekRange = weekRange;
            this.progress = progress;
        }
    }
}