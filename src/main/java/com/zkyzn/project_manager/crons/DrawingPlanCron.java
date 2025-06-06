package com.zkyzn.project_manager.crons;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时处理图纸计划的数据初始化
 * 将文件目录中的图纸计划读取并清洗到数据库中
 */
@Component
public class DrawingPlanCron {

    /**
     * 每隔一段时间处理图纸计划相关数据
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void ProcessDrawingPlan() {

    }
}
