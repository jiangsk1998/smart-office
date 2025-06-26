package com.zkyzn.project_manager.so.project.overview;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/24 17:23
 */
@Data
public class MonthlyProgress {

    /**
     * 当前本月项目进度百分比
     */
    private BigDecimal currentRate;

    /**
     * 与前一天相比的变化值
     */
    private BigDecimal dailyChange;

    /**
     * 最近10天本月项目进度数据（包含日期和值）
     */
    private List<DailyData> last10Days;

    @Data
    public static class DailyData {
        /**
         * 日期
         */
        private LocalDate date;
        /**
         * 项目进度百分比
         */
        private BigDecimal value;
    }
}
