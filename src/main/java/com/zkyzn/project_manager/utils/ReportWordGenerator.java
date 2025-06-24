package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.models.message.ReportContent;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportWordGenerator {

    /**
     * 生成报告内容的Word文档。
     *
     * @param reportContent 包含报告数据的ReportContent对象
     * @param fileNamePrefix 生成的Word文档的文件名前缀
     * @return 生成的Word文档的完整路径，如果生成失败则返回null
     */
    public static String generateReportWord(ReportContent reportContent, String fileNamePrefix) {
        // 创建一个新的空白Word文档
        try (XWPFDocument document = new XWPFDocument()) {

            // 设置文档标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("计划报告");
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.addBreak(); // 添加一个换行

            // 添加报告基本信息
            addParagraph(document, "报告类型: " + reportContent.getReportType());
            addParagraph(document, "报告周期: " + reportContent.getReportPeriod());
            addParagraph(document, "报告所属人: " + reportContent.getPersonName());
            addParagraph(document, ""); // 空行

            // 添加任务列表（如果存在）
            if (reportContent.getReportType().equalsIgnoreCase("WEEKLY") || reportContent.getReportType().equalsIgnoreCase("MONTHLY")) {
                addTaskList(document, "本期已完成任务", reportContent.getCompletedTasks());
                addTaskList(document, "本期未完成任务", reportContent.getUncompletedTasks());
                addTaskList(document, "下期应完成任务", reportContent.getNextPeriodTasks());
            }

            // 添加任务汇总信息
            addParagraph(document, "--- 任务汇总 ---");
            addParagraph(document, "本期已完成任务总数: " + reportContent.getTotalCompletedTasks());
            addParagraph(document, "本期未完成任务总数: " + reportContent.getTotalUncompletedTasks());
            addParagraph(document, "下期应完成任务总数: " + reportContent.getTotalNextPeriodTasks());
            addParagraph(document, ""); // 空行

            // 生成文件名
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s.docx", fileNamePrefix, timestamp);

            // 获取当前工作目录，并将文件保存到此处
            String userDir = System.getProperty("user.dir");
            String filePath = Paths.get(userDir, fileName).toString();

            // 将文档写入文件
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }

            System.out.println("Word文档生成成功: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.err.println("生成Word文档时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 辅助方法：向文档中添加一个段落
     * @param document XWPFDocument对象
     * @param text 段落文本
     */
    private static void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(11);
    }

    /**
     * 辅助方法：向文档中添加任务列表
     * @param document XWPFDocument对象
     * @param title 列表标题
     * @param tasks 任务列表
     */
    private static void addTaskList(XWPFDocument document, String title, List<ReportContent.TaskItem> tasks) {
        addParagraph(document, "--- " + title + " ---");
        if (tasks != null && !tasks.isEmpty()) {
            XWPFTable table = document.createTable(tasks.size() + 1, 6); // +1 用于表头，6列
            table.setWidth("100%"); // 设置表格宽度为100%

            // 设置表头
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow.getCell(0), "项目名称");
            setHeaderCell(headerRow.getCell(1), "任务包/所属阶段");
            setHeaderCell(headerRow.getCell(2), "任务描述");
            setHeaderCell(headerRow.getCell(3), "开始日期");
            setHeaderCell(headerRow.getCell(4), "结束日期/计划完成日期");
            setHeaderCell(headerRow.getCell(5), "状态");

            // 填充数据
            for (int i = 0; i < tasks.size(); i++) {
                ReportContent.TaskItem task = tasks.get(i);
                XWPFTableRow row = table.getRow(i + 1);
                setTableCell(row.getCell(0), task.getProjectName());
                setTableCell(row.getCell(1), task.getTaskPackage());
                setTableCell(row.getCell(2), task.getTaskDescription());
                setTableCell(row.getCell(3), task.getStartDate());
                setTableCell(row.getCell(4), task.getEndDate());
                setTableCell(row.getCell(5), task.getStatus());
            }
        } else {
            addParagraph(document, "无");
        }
        addParagraph(document, ""); // 空行
    }

    /**
     * 辅助方法：设置表格单元格内容和样式
     * @param cell XWPFTableCell对象
     * @param text 单元格文本
     */
    private static void setTableCell(XWPFTableCell cell, String text) {
        // 清除单元格中默认的段落
        if (cell.getParagraphs().isEmpty()) {
            cell.addParagraph();
        }
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        run.setFontSize(10);
    }

    /**
     * 辅助方法：设置表格表头单元格内容和样式
     * @param cell XWPFTableCell对象
     * @param text 表头文本
     */
    private static void setHeaderCell(XWPFTableCell cell, String text) {
        // 清除单元格中默认的段落
        if (cell.getParagraphs().isEmpty()) {
            cell.addParagraph();
        }
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(10);
    }
}