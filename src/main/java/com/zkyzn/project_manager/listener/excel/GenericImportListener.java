package com.zkyzn.project_manager.listener.excel;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用导入监听器，将读取的数据存储到列表中
 * @author Mr-ti
 */
public class GenericImportListener<T> implements ReadListener<T> {
    private final List<T> dataList = new ArrayList<>();

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("导入完成，共读取: " + dataList.size() + " 条数据");
    }

    /**
     * 获取数据
     * @return List<T>
     */
    public List<T> getData() {
        return dataList;
    }
}
