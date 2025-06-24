package com.zkyzn.project_manager.so.project.overview;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/24 17:24
 */
@Data
public class PersonnelData {

    /**
     * 当前责任人总数
     */
    private Integer count;

    /**
     * 与前一天相比的变化百分比
     */
    private BigDecimal dailyChangePercentage;

    /**
     * 最近10天责任人总数列表
     */
    private List<Integer> last10Days;
}
