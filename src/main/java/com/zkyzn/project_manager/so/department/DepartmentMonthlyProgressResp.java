package com.zkyzn.project_manager.so.department;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 科室月工作完成进度响应体
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "科室月工作完成进度响应体")
public class DepartmentMonthlyProgressResp {

    @Schema(description = "本月工作完成进度（百分比）")
    private BigDecimal thisMonthProgress;

    @Schema(description = "当日本月进度相比昨日的变化百分比 (例如: -10.2% / +10.2%)")
    private String dailyChangePercentage;

    @Schema(description = "近10日的每日月度进度（用于柱状图）")
    private List<DailyProgress> last10DaysProgress;

    /**
     * 内部类，用于表示每日的进度
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "每日进度")
    public static class DailyProgress {

        @Schema(description = "日期 (格式: yyyy-MM-dd)")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate date;

        @Schema(description = "截止当日的月度完成进度（百分比）")
        private BigDecimal progress;

        public DailyProgress(LocalDate date, BigDecimal progress) {
            this.date = date;
            this.progress = progress;
        }
    }
}