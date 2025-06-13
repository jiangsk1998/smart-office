package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;

import java.time.LocalDate;

/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:15
 */
@Data
public class ChangeRecord {
    // 变更项名称
    private String itemName;

    // 变更前内容
    private String beforeChange;

    // 变更后内容
    private String afterChange;

    // 变更人
    private String changedBy;

    // 变更时间
    private LocalDate changeDate;
}
