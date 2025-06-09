package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/8 22:25
 */
public class ExcelUtil {

    /**
     * 解析项目计划表
     * @param filePath Excel文件路径
     * @return 项目计划项列表
     */
    public static List<ProjectPlan> parseProjectPlan(String filePath, Long projectId) {
        List<ProjectPlan> planItems = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 获取第一个工作表（项目计划）
            Sheet sheet = workbook.getSheetAt(0);

            // 从第二行开始读取（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ProjectPlan item = parseProjectPlanRow(row);
                if (item != null) {
                    item.setProjectId(projectId);
                    planItems.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析Excel文件失败: " + e.getMessage(), e);
        }

        return planItems;
    }

    /**
     * 解析项目计划单行数据
     */
    private static ProjectPlan parseProjectPlanRow(Row row) {
        // 跳过空行
        if (isRowEmpty(row)) {
            return null;
        }

        ProjectPlan projectPlan = new ProjectPlan();

        // 序号（A列）
        projectPlan.setTaskOrder(getNumericValue(row, 0));

        // 任务包（B列）
        projectPlan.setTaskPackage(getStringValue(row, 1));

        // 任务内容（C列）
        projectPlan.setTaskDescription(getStringValue(row, 2));

        // 开始时间（D列）
        projectPlan.setStartDate(getDateValue(row, 3));

        // 结束时间（E列）
        projectPlan.setEndDate(getDateValue(row, 4));

        // 责任人（F列）
        projectPlan.setResponsiblePerson(getStringValue(row, 5));

        // 科室（G列）
        projectPlan.setDepartment(getStringValue(row, 6));

        // 成果（H列）
        projectPlan.setDeliverable(getStringValue(row, 7));

        // 成果类型（I列）
        projectPlan.setDeliverableType(getStringValue(row, 8));

        // 里程碑（J列）
        projectPlan.setIsMilestone(isMilestone(getStringValue(row, 9)));

        return projectPlan;
    }

    /**
     * 解析项目信息导入表
     * @param filePath Excel文件路径
     * @return 项目信息列表
     */
    public static List<ProjectInfo> parseProjectInfoSheet(String filePath, Long creatorId) {
        List<ProjectInfo> infoItems = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 获取第一个工作表（项目计划）
            Sheet sheet = workbook.getSheetAt(0);

            // 从第二行开始读取（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ProjectInfo item = parseProjectInfoRow(row);
                if (item != null) {
                    item.setCreatorId(creatorId);
                    infoItems.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析Excel文件失败: " + e.getMessage(), e);
        }

        return infoItems;
    }

    /**
     * 解析项目信息单行数据
     */
    private static ProjectInfo parseProjectInfoRow(Row row) {
        // 跳过空行
        if (isRowEmpty(row)) {
            return null;
        }

        ProjectInfo projectInfo = new ProjectInfo();

        // 项目工号（A列）
        projectInfo.setProjectNumber(getStringValue(row, 0));

        // 项目名称（B列）
        projectInfo.setProjectName(getStringValue(row, 1));

        // 所属科室（C列）
        projectInfo.setDepartment(getStringValue(row, 2));

        // 立项时间（D列）
        projectInfo.setStartDate(getDateValue(row, 3));

        // 结束时间（E列）
        projectInfo.setEndDate(getDateValue(row, 4));

        // 分管领导（F列）
        projectInfo.setResponsibleLeaderId(getNumericLongValue(row, 5));

        // 技术负责人（G列）
        projectInfo.setTechnicalLeaderId(getNumericLongValue(row, 6));

        // 计划主管（H列）
        projectInfo.setPlanSupervisorId(getNumericLongValue(row, 7));

        return projectInfo;
    }

    /**
     * 检查行是否为空
     */
    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        // 检查前10列
        for (int i = 0; i < 10; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取字符串值
     */
    private static String getStringValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }

    /**
     * 获取数值
     */
    private static Integer getNumericValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取数值
     */
    private static Long getNumericLongValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取日期值
     */
    private static LocalDate getDateValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }

    /**
     * 判断是否为里程碑
     */
    private static boolean isMilestone(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return "是".equalsIgnoreCase(value) ||
                "true".equalsIgnoreCase(value) ||
                "√".equalsIgnoreCase(value) ||
                "里程碑".equalsIgnoreCase(value);
    }
}
