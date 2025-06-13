package com.zkyzn.project_manager.so.project.dashboard;


import lombok.Data;

import java.time.LocalDate;

/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:15
 */
@Data
public class UpcomingTask {
    // 任务内容
    private String taskContent;

    // 负责人
    private String responsiblePerson;

    // 结束日期
    private LocalDate endDate;

    // 优先级（高/中/低）
    private String priority;
}
