package com.zkyzn.project_manager.excel;

import cn.idev.excel.annotation.ExcelProperty;
import com.zkyzn.project_manager.converts.date.LocalDateConverter;
import com.zkyzn.project_manager.converts.date.LocalDateTimeConverter;
import com.zkyzn.project_manager.converts.date.LocalTimeConverter;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 测试excel bean
 * @author Zhang Fan
 */
@Data
public class TestExcelBean {

    @ExcelProperty("序号")
    private String index;

    @ExcelProperty(value = "日期", converter = LocalDateConverter.class)
    private LocalDate date;

    @ExcelProperty(value = "日期时间", converter = LocalDateTimeConverter.class)
    private LocalDateTime datetime;

    @ExcelProperty(value = "时间", converter = LocalTimeConverter.class)
    private LocalTime time;
}
