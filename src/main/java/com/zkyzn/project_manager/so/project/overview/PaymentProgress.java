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
public class PaymentProgress {

    /**
     * 当前进款进度百分比
     */
    private BigDecimal currentRate;

    /**
     * 与前一天相比的变化百分比
     */
    private BigDecimal dailyChange;

    /**
     * 最近10天进款进度数据（包含日期和值）
     */
    private List<DailyData> last10Days;

    @Data
    public static class DailyData {
        /**
         * 日期
         */
        private LocalDate date;
        /**
         * 进款进度百分比
         */
        private BigDecimal value;
    }

}
