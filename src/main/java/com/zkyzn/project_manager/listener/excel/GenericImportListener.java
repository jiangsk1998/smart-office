package com.zkyzn.project_manager.listener.excel;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用导入监听器，将读取的数据存储到列表中
 * @author Mr-ti
 */
public class GenericImportListener<T> implements ReadListener<T> {

    private final List<T> dataList = new ArrayList<>();
    private final Set<Integer> skipRows;

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        int rowIndex = analysisContext.readRowHolder().getRowIndex();

        // 跳过指定行
        if (skipRows.contains(rowIndex)) {
            return;
        }
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("导入完成，共读取: " + dataList.size() + " 条数据");
    }

    public GenericImportListener(Set<Integer> skipRows) {
        this.skipRows = skipRows != null ? skipRows : new HashSet<>();
    }

    /**
     * 获取数据
     * @return List<T>
     */
    public List<T> getData() {
        return dataList;
    }
}
