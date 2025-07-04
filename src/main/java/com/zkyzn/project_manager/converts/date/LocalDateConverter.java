package com.zkyzn.project_manager.converts.date;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.DataFormatData;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.format.DataFormatter;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * 日期格式转换器
 *
 * @author Zhang Fan
 */
@Slf4j
public class LocalDateConverter implements Converter<LocalDate> {

    private static final DateTimeFormatter FMT1 = DateTimeFormatter.ofPattern("yyyy-M-d");
    private static final DateTimeFormatter FMT2 = DateTimeFormatter.ofPattern("yyyy/M/d");

    @Override
    public Class<LocalDate> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDate value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(FMT1.format(value));
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String dateStr = cellData.getStringValue();
        DataFormatData dataFormat = cellData.getDataFormatData();
        if (StringUtils.isBlank(dateStr) && dataFormat != null) {
            dateStr = new DataFormatter(Boolean.FALSE, Locale.CHINA, Boolean.FALSE).format(cellData.getNumberValue(), dataFormat.getIndex(), dataFormat.getFormat());
        }
        try {
            return LocalDate.parse(dateStr, FMT1);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr, FMT2);
            } catch (DateTimeParseException ex) {
                log.error("无法解析的日期格式: {}. 应为 yyyy-MM-dd 或 yyyy/MM/dd.", dateStr);
                throw new RuntimeException("无法解析的日期格式: " + dateStr + ". 应为 yyyy-MM-dd 或 yyyy/MM/dd.", ex);
            }
        }
    }
}
