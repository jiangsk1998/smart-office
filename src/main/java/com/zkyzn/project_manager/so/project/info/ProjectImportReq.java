package com.zkyzn.project_manager.so.project.info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Mr-ti
 * Date: 2025/6/9 15:49
 */
@Schema(description = "批量导入项目请求")
@Data
public class ProjectImportReq {

    @Schema(description = "导入Excel文件路径")
    private String importExcelFilePath;

}
