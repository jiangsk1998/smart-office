package com.zkyzn.project_manager.converts.imports;

import cn.idev.excel.annotation.ExcelProperty;
import com.zkyzn.project_manager.models.ContractNode;
import lombok.Data;
/**
 * @author: Mr-ti
 * Date: 2025/6/20 12:07
 */
@Data
public class ContractNodeExcel extends ContractNode {

    @ExcelProperty("序号")
    private String serialNumber;
}
