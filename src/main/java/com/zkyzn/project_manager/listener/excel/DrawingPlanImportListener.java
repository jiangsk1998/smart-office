package com.zkyzn.project_manager.listener.excel;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import com.zkyzn.project_manager.converts.imports.DrawingPlanExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现对图纸计划的读取
 */
public class DrawingPlanImportListener implements ReadListener<DrawingPlanExcel> {
    private final List<DrawingPlanExcel> dataList = new ArrayList<>();


    @Override
    public void invoke(DrawingPlanExcel drawingPlanExcel, AnalysisContext analysisContext) {
        dataList.add(drawingPlanExcel);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("导入完成，共读取: " + dataList.size() + " 条数据");
    }

    /**
     * 获取数据
     * @return List<DrawingPlanExcel>
     */
    public List<DrawingPlanExcel> getData() {
        return dataList;
    }
}
