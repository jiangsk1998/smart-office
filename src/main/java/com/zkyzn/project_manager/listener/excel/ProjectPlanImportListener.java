package com.zkyzn.project_manager.listener.excel;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CellExtraTypeEnum;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.metadata.CellExtra;
import com.zkyzn.project_manager.converts.imports.ProjectPlanExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr-ti
 */
public class ProjectPlanImportListener extends AnalysisEventListener<ProjectPlanExcel> {
    private final List<ProjectPlanExcel> dataList = new ArrayList<>();
    private final List<CellExtra> mergeInfoList = new ArrayList<>();

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        if (extra.getType() == CellExtraTypeEnum.MERGE) {
            mergeInfoList.add(extra);
        }
    }

    @Override
    public void invoke(ProjectPlanExcel rowData, AnalysisContext context) {
        dataList.add(rowData);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        // 获取当前处理的工作表信息
        int headRowNumber = context.readSheetHolder().getHeadRowNumber();

        // 处理所有合并区域
        for (CellExtra merge : mergeInfoList) {
            int firstRowIndex = merge.getFirstRowIndex();
            int lastRowIndex = merge.getLastRowIndex();
            int firstColIndex = merge.getFirstColumnIndex();
            int lastColIndex = merge.getLastColumnIndex();

            // 跳过表头区域和非数据行
            if (firstRowIndex < headRowNumber) {
                continue;
            }

            // 只处理单列合并（更安全）
            if (firstColIndex != lastColIndex) {
                System.out.printf("跳过跨列合并区域: %d-%d行, %d-%d列%n",
                        firstRowIndex, lastRowIndex, firstColIndex, lastColIndex);
                continue;
            }

            int dataStartRow = firstRowIndex - headRowNumber;
            int dataEndRow = Math.min(lastRowIndex - headRowNumber, dataList.size() - 1);

            // 获取合并区域主值（仅获取一次）
            ProjectPlanExcel firstData = dataList.get(dataStartRow);
            Object mergeValue = getValueByColumnIndex(firstData, firstColIndex);

            // 填充合并区域
            for (int rowIndex = dataStartRow + 1; rowIndex <= dataEndRow; rowIndex++) {
                ProjectPlanExcel currentData = dataList.get(rowIndex);
                if (getValueByColumnIndex(currentData, firstColIndex) == null) {
                    setValueByColumnIndex(currentData, firstColIndex, mergeValue);
                }
            }
        }
        System.out.println("项目计划导入完成，共读取: " + dataList.size() + " 条数据");
    }

    private Object getValueByColumnIndex(ProjectPlanExcel data, int columnIndex) {
        switch (columnIndex) {
            case 0: return data.getSerialNumber();
            case 1: return data.getTaskPackage();
            case 2: return data.getTaskContent();
            case 3: return data.getStartDate();
            case 4: return data.getEndDate();
            case 5: return data.getResponsiblePerson();
            case 6: return data.getDepartment();
            case 7: return data.getAchievement();
            case 8: return data.getAchievementType();
            case 9: return data.getMilestone();
            default: return null;
        }
    }

    private void setValueByColumnIndex(ProjectPlanExcel data, int columnIndex, Object value) {
        if (value == null) {
            return;
        }

        String stringValue = value.toString();
        switch (columnIndex) {
            case 0: data.setSerialNumber(stringValue); break;
            case 1: data.setTaskPackage(stringValue); break;
            case 2: data.setTaskContent(stringValue); break;
            case 3: data.setStartDate(stringValue); break;
            case 4: data.setEndDate(stringValue); break;
            case 5: data.setResponsiblePerson(stringValue); break;
            case 6: data.setDepartment(stringValue); break;
            case 7: data.setAchievement(stringValue); break;
            case 8: data.setAchievementType(stringValue); break;
            case 9: data.setMilestone(stringValue); break;
        }
    }

    public List<ProjectPlanExcel> getData() {
        return new ArrayList<>(dataList);
    }
}