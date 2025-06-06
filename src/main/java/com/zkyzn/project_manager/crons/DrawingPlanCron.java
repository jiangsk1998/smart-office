package com.zkyzn.project_manager.crons;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * 定时处理图纸计划的数据初始化
 * 将文件目录中的图纸计划读取并清洗到数据库中
 */
@Component
public class DrawingPlanCron {


    @Value("${file.old.plan}")
    String oldPlanDir;


    /**
     * 每隔一段时间处理图纸计划相关数据
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void ProcessDrawingPlan() throws IOException {
        // 读取历史图纸列表
        try (Stream<Path> paths = Files.list(Paths.get(oldPlanDir))) {
            paths.filter(p -> p.toString().endsWith(".xls"))
                    .forEach(p -> {

                    });
        }
    }
}
