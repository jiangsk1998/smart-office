package com.zkyzn.project_manager.converts.date;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.converters.ReadConverterContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * @author: Mr-ti
 * Date: 2025/6/20 19:20
 */
public class LocalDateConverter implements Converter<LocalDate> {

    // Excel中的日期格式：2024年01月31日
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    // 匹配日期格式：2024年01月31日
    private static final Pattern DATE_PATTERN =
            Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日");



    @Override
    public LocalDate convertToJavaData(ReadConverterContext<?> cellData) {

        // 1. 检查空值
        if (null == cellData || null == cellData.getReadCellData() || null == cellData.getReadCellData().getStringValue()) {
            return null;
        }

        String dateString = cellData.getReadCellData().getStringValue().trim();

        // 2. 检查是否为空字符串
        if (dateString.isEmpty()) {
            return null;
        }

        // 3. 检查是否符合日期格式
        if (!DATE_PATTERN.matcher(dateString).matches()) {
            System.err.println("警告: 忽略非日期格式内容: " + dateString);
            return null;
        }

        // 4. 尝试转换
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("日期解析错误: " + dateString + " - " + e.getMessage());
            return null;
        }
    }
}
