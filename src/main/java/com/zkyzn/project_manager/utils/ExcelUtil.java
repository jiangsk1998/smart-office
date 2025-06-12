package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.UserInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * @author: Mr-ti
 * Date: 2025/6/8 22:25
 */
public class ExcelUtil {

    /**
     * 解析项目计划表（优化合并单元格处理）
     * @param filePath Excel文件路径
     * @return 项目计划项列表
     */
    public static List<ProjectPlan> parseProjectPlan(String filePath, Long projectId, List<String> responsiblePersons) {
        List<ProjectPlan> planItems = new ArrayList<>();
        // 使用 Set 进行临时去重
        Set<String> uniquePersons = new HashSet<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 获取第一个工作表（项目计划）
            Sheet sheet = workbook.getSheetAt(0);

            // 预加载合并区域信息
            Map<String, String> mergedCellValues = getMergedCellValues(sheet);

            // 从第二行开始读取（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ProjectPlan item = parseProjectPlanRow(row, mergedCellValues);
                if (item != null) {
                    item.setProjectId(projectId);
                    planItems.add(item);

                    // 收集责任人（F列）
                    String person = item.getResponsiblePerson();
                    if (person != null && !person.isEmpty()) {
                        uniquePersons.add(person);
                    }
                }
            }

            // 将去重后的责任人添加到传入的列表
            if (responsiblePersons != null) {
                responsiblePersons.addAll(uniquePersons);
                // 对列表进行排序
                Collections.sort(responsiblePersons);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析Excel文件失败: " + e.getMessage(), e);
        }

        return planItems;
    }

    /**
     * 获取合并单元格的值映射
     */
    private static Map<String, String> getMergedCellValues(Sheet sheet) {
        Map<String, String> mergedValues = new HashMap<>();

        // 获取所有合并区域
        int numMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);

            // 获取合并区域的第一个单元格（包含实际值）
            Row firstRow = sheet.getRow(region.getFirstRow());
            if (firstRow == null) {
                continue;
            }

            Cell firstCell = firstRow.getCell(region.getFirstColumn());
            if (firstCell == null) {
                continue;
            }

            // 获取单元格值
            String cellValue = getCellStringValue(firstCell);

            // 将合并区域所有单元格映射到该值
            for (int rowIdx = region.getFirstRow(); rowIdx <= region.getLastRow(); rowIdx++) {
                for (int colIdx = region.getFirstColumn(); colIdx <= region.getLastColumn(); colIdx++) {
                    String key = rowIdx + "_" + colIdx;
                    mergedValues.put(key, cellValue);
                }
            }
        }
        return mergedValues;
    }

    /**
     * 解析项目计划单行数据（含处理合并单元格）
     */
    private static ProjectPlan parseProjectPlanRow(Row row, Map<String, String> mergedCellValues) {
        // 跳过空行
        if (isRowEmpty(row)) {
            return null;
        }

        ProjectPlan projectPlan = new ProjectPlan();
        int rowNum = row.getRowNum();

        // 序号（A列）
        projectPlan.setTaskOrder(Float.valueOf(getNumericValue(row, 0, mergedCellValues)));

        // 任务包（B列）- 重点处理合并单元格
        projectPlan.setTaskPackage(getStringValue(row, 1, mergedCellValues));

        // 任务内容（C列）
        projectPlan.setTaskDescription(getStringValue(row, 2, mergedCellValues));

        // 开始时间（D列）
        projectPlan.setStartDate(getDateValue(row, 3, mergedCellValues));

        // 结束时间（E列）
        projectPlan.setEndDate(getDateValue(row, 4, mergedCellValues));

        // 责任人（F列）
        projectPlan.setResponsiblePerson(getStringValue(row, 5, mergedCellValues));

        // 科室（G列）
        projectPlan.setDepartment(getStringValue(row, 6, mergedCellValues));

        // 成果（H列）
        projectPlan.setDeliverable(getStringValue(row, 7, mergedCellValues));

        // 成果类型（I列）
        projectPlan.setDeliverableType(getStringValue(row, 8, mergedCellValues));

        // 里程碑（J列）
        projectPlan.setIsMilestone(isMilestone(getStringValue(row, 9, mergedCellValues)));

        return projectPlan;
    }

    /**
     * 解析项目信息导入表
     * @param filePath Excel文件路径
     * @param creatorName 创建人姓名
     * @return 项目信息列表
     */
    public static List<ProjectInfo> parseProjectInfoSheet(String filePath, String creatorName) {
        List<ProjectInfo> infoItems = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 获取第一个工作表（项目计划）
            Sheet sheet = workbook.getSheetAt(0);

            // 预加载合并区域信息
            Map<String, String> mergedCellValues = getMergedCellValues(sheet);

            // 从第二行开始读取（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ProjectInfo item = parseProjectInfoRow(row, mergedCellValues);
                if (item != null) {
                    item.setCreatorName(creatorName);
                    infoItems.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析Excel文件失败: " + e.getMessage(), e);
        }

        return infoItems;
    }

    /**
     * 解析项目信息单行数据（含处理合并单元格）
     */
    private static ProjectInfo parseProjectInfoRow(Row row, Map<String, String> mergedCellValues) {
        // 跳过空行
        if (isRowEmpty(row)) {
            return null;
        }

        ProjectInfo projectInfo = new ProjectInfo();

        // 项目工号（A列）
        projectInfo.setProjectNumber(getStringValue(row, 0, mergedCellValues));

        // 项目名称（B列）
        projectInfo.setProjectName(getStringValue(row, 1, mergedCellValues));

        // 所属科室（C列）
        projectInfo.setDepartment(getStringValue(row, 2, mergedCellValues));

        // 立项时间（D列）
        projectInfo.setStartDate(getDateValue(row, 3, mergedCellValues));

        // 结束时间（E列）
        projectInfo.setEndDate(getDateValue(row, 4, mergedCellValues));

        // 分管领导（F列）
        projectInfo.setResponsibleLeader(getStringValue(row, 5, mergedCellValues));

        // 技术负责人（G列）
        projectInfo.setTechnicalLeader(getStringValue(row, 6, mergedCellValues));

        // 计划主管（H列）: 多个名字以空格分隔
        String supervisorsStr = getStringValue(row, 7, mergedCellValues);
        if (supervisorsStr != null && !supervisorsStr.trim().isEmpty()) {
            List<UserInfo> supervisorList = new ArrayList<>();
            // 按空格分割并去除空值
            String[] names = supervisorsStr.split("\\s+");
            for (String name : names) {
                if (!name.trim().isEmpty()) {
                    UserInfo user = new UserInfo();
                    // 仅设置用户账号（姓名）
                    user.setUserAccount(name.trim());
                    supervisorList.add(user);
                }
            }
            projectInfo.setPlanSupervisors(supervisorList);
        } else {
            projectInfo.setPlanSupervisors(Collections.emptyList());
        }

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
     * 获取字符串值（处理合并单元格）
     */
    private static String getStringValue(Row row, int columnIndex, Map<String, String> mergedCellValues) {
        String key = row.getRowNum() + "_" + columnIndex;

        // 检查是否是合并单元格的一部分
        if (mergedCellValues.containsKey(key)) {
            return mergedCellValues.get(key);
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return getCellStringValue(cell);
    }

    /**
     * 获取单元格的字符串值
     */
    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            }
            double num = cell.getNumericCellValue();
            if (num == (int) num) {
                return String.valueOf((int) num);
            }
            return String.valueOf(num);
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            return cell.getCellFormula();
        }
        return "";
    }

    /**
     * 获取数值（处理合并单元格）
     */
    private static Integer getNumericValue(Row row, int columnIndex, Map<String, String> mergedCellValues) {
        String key = row.getRowNum() + "_" + columnIndex;

        // 检查是否是合并单元格的一部分
        if (mergedCellValues.containsKey(key)) {
            String value = mergedCellValues.get(key);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return getCellNumericValue(cell);
    }

    /**
     * 获取单元格的整数值
     */
    private static Integer getCellNumericValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (cell.getCellType() == CellType.FORMULA) {
            try {
                return (int) cell.getNumericCellValue();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取长整型数值（处理合并单元格）
     */
    private static Long getNumericLongValue(Row row, int columnIndex, Map<String, String> mergedCellValues) {
        String key = row.getRowNum() + "_" + columnIndex;

        // 检查是否是合并单元格的一部分
        if (mergedCellValues.containsKey(key)) {
            String value = mergedCellValues.get(key);
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return getCellLongValue(cell);
    }

    /**
     * 获取单元格的长整型值
     */
    private static Long getCellLongValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (cell.getCellType() == CellType.FORMULA) {
            try {
                return (long) cell.getNumericCellValue();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取日期值（处理合并单元格）
     */
    private static LocalDate getDateValue(Row row, int columnIndex, Map<String, String> mergedCellValues) {
        String key = row.getRowNum() + "_" + columnIndex;

        // 检查是否是合并单元格的一部分
        if (mergedCellValues.containsKey(key)) {
            // 对于日期字段，合并单元格通常不会包含日期值，所以这里不处理
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return getCellDateValue(cell);
    }

    /**
     * 获取单元格的日期值
     */
    private static LocalDate getCellDateValue(Cell cell) {
        if (cell == null) {
            return null;
        }

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