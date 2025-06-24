package com.zkyzn.project_manager.excel;

import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import com.zkyzn.project_manager.utils.JsonUtil;

/**
 * @author Zhang Fan
 */
public class TestExcelMain {

    public static void main(String[] args) {
        String fileName = "src/test/java/com/zkyzn/project_manager/excel/test.xlsx";
        // Read the first sheet
        FastExcel.read(fileName, TestExcelBean.class, new ReadListener<TestExcelBean> () {

            @Override
            public void invoke(TestExcelBean testExcelBean, AnalysisContext analysisContext) {
                System.out.println(JsonUtil.toJson(testExcelBean));
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).sheet().headRowNumber(1).doRead();
    }
}
