package com.zkyzn.project_manager.converts.imports;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Mr-ti
 */
@Data
public class ProjectPlanExcel {

    @ExcelProperty("序号")
    private String serialNumber;

    @ExcelProperty("任务包")
    private String taskPackage;

    @ExcelProperty("任务内容")
    private String taskContent;

    @ExcelProperty("开始时间")
    private String startDate;

    @ExcelProperty("结束时间")
    private String endDate;

    @ExcelProperty("责任人")
    private String responsiblePerson;

    @ExcelProperty("科室")
    private String department;

    @ExcelProperty("成果")
    private String achievement;

    @ExcelProperty("成果类型")
    private String achievementType;

    @ExcelProperty("里程碑")
    private String milestone;
}