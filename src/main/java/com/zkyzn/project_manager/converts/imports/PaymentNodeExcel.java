package com.zkyzn.project_manager.converts.imports;

import cn.idev.excel.annotation.ExcelProperty;
import com.zkyzn.project_manager.models.PaymentNode;
import lombok.Data;
/**
 * @author: Mr-ti
 * Date: 2025/6/20 12:18
 */
@Data
public class PaymentNodeExcel extends PaymentNode {

    @ExcelProperty("序号")
    private String serialNumber;
}
