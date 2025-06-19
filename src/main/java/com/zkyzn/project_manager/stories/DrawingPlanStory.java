package com.zkyzn.project_manager.stories;

import cn.idev.excel.FastExcel;
import cn.idev.excel.enums.CellExtraTypeEnum;
import com.zkyzn.project_manager.converts.imports.DrawingPlanExcel;
import com.zkyzn.project_manager.listener.excel.GenericImportListener;
import com.zkyzn.project_manager.models.OldDrawingPlan;
import com.zkyzn.project_manager.services.OldDrawingPlanService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;


/**
 * 图纸计划相关
 */
@Service
public class DrawingPlanStory {

    @Value("${file.old.plan}")
    String oldPlanDir;

    @Value("${file.root.path:./}")
    private String fileRootPath;

    @Resource
    private OldDrawingPlanService oldDrawingPlanService;

    public String generateDrawingPlan(String key, LocalDate start, Long days)  throws Exception {
        OldDrawingPlan oldDrawingPlan = oldDrawingPlanService.getByKey(key);
        if (oldDrawingPlan == null) {
            throw new Exception("未找到该图纸");
        }
        long totalDays = oldDrawingPlan.getPlanEndDate().until(oldDrawingPlan.getPlanStartDate(), ChronoUnit.DAYS);
        // 计算缩放比例
        double scaleRatio = (double) totalDays / days;

        Path filePath = Paths.get(oldPlanDir, oldDrawingPlan.getDrawingPlanName());
        String timeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String relativeTempPath = Paths.get("drawing_plan" , timeString + oldDrawingPlan.getDrawingPlanName()).toString();
        Path newFilePath = Paths.get(fileRootPath,relativeTempPath);
        // 保存文件到本地
        Files.createDirectories(newFilePath.getParent()); // 创建目录（如果不存在）

        // 读取历史的Excel表格
        GenericImportListener<DrawingPlanExcel> listener = new GenericImportListener();
        FastExcel.read(filePath.toFile(), DrawingPlanExcel.class, listener)
                .extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();

        listener.getData().stream().forEach(plan -> {

        });

        try (FileOutputStream fos = new FileOutputStream(newFilePath.toFile())) {
            // 核心写入逻辑
            FastExcel.write(fos, DrawingPlanExcel.class)
                    .sheet("数据表")         // 设置工作表名称
                    .doWrite( listener.getData());     // 写入数据集合
        }

        return relativeTempPath;
    }
}
