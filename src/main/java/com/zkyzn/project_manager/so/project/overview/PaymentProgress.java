package com.zkyzn.project_manager.so.project.overview;


import lombok.Data;

import java.math.BigDecimal;
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
     * 最近10天进款进度列表
     */
    private List<BigDecimal> last10Days;
}
