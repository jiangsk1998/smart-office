package com.zkyzn.project_manager.so.project.overview;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/24 17:23
 */
@Data
public class OverdueProjects {

    /**
     * 当前超期项目个数
     */
    private Long count;

    /**
     * 与前一天相比的变化百分比
     */
    private BigDecimal dailyChangePercentage;

    /**
     * 最近10天超期项目个数列表
     */
    private List<Long> last10Days;
}
