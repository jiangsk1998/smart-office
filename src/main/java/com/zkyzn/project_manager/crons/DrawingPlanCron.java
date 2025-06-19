package com.zkyzn.project_manager.crons;

import cn.idev.excel.FastExcel;
import cn.idev.excel.enums.CellExtraTypeEnum;
import com.zkyzn.project_manager.converts.imports.DrawingPlanExcel;
import com.zkyzn.project_manager.listener.excel.GenericImportListener;
import com.zkyzn.project_manager.models.OldDrawingPlan;
import com.zkyzn.project_manager.services.OldDrawingPlanService;
import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

/**
 * 定时处理图纸计划的数据初始化
 * 将文件目录中的图纸计划读取并清洗到数据库中
 */
@Component
public class DrawingPlanCron {

    @Value("${file.old.plan}")
    String oldPlanDir;

    @Resource
    private OldDrawingPlanService  oldDrawingPlanService;

    /**
     * 每隔一段时间处理图纸计划相关数据
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void ProcessDrawingPlan() throws IOException {
        List<OldDrawingPlan> allPlan = oldDrawingPlanService.list();

        // 读取历史图纸列表
        try (Stream<Path> paths = Files.list(Paths.get(oldPlanDir))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
            paths.filter(p -> p.toString().endsWith(".xls"))
                    .forEach(path -> {
                        String md5String = DigestUtils.md5Hex(path.toFile().getAbsolutePath());
                        if(allPlan.stream().noneMatch(plan -> plan.getFileHash().equals(md5String))) {
                            GenericImportListener<DrawingPlanExcel> listener = new GenericImportListener<>();
                            FastExcel.read(path.toFile(), DrawingPlanExcel.class, listener)
                                    .extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
                            List<LocalDate> planDateStream = listener
                                    .getData()
                                    .stream()
                                    .map(DrawingPlanExcel::getPlanDate)
                                    .map(time -> LocalDate.parse(time, formatter))
                                    .toList();
                            LocalDate maxDate = planDateStream.stream().max(LocalDate::compareTo).orElse(LocalDate.now());
                            LocalDate minDate = planDateStream.stream().min(LocalDate::compareTo).orElse(LocalDate.now());

                            OldDrawingPlan oldDrawingPlan = new OldDrawingPlan();
                            oldDrawingPlan.setFileHash(md5String);
                            oldDrawingPlan.setDrawingPlanName(path.getFileName().toString());
                            oldDrawingPlan.setPlanStartDate(minDate);
                            oldDrawingPlan.setPlanEndDate(maxDate);
                            oldDrawingPlanService.save(oldDrawingPlan);
                            System.out.println("导入成功，数据量: " + listener.getData().size());
                        }
                    });
        }
    }
}
