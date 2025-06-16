package com.zkyzn.project_manager.converts.imports;


import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrawingPlanExcel {

    @ExcelProperty("序号")
    private String SerialNumber;

    @ExcelProperty("图号")
    private String DrawingNumber;

    @ExcelProperty("名称")
    private String DrawingName;

    @ExcelProperty("密级")
    private String SecurityLevel;

    @ExcelProperty("审签流程")
    private String ApprovalWorkflow;

    @ExcelProperty("计划时间")
    private String planDate;

    @ExcelProperty("状态")
    private String status;
}
