package com.zkyzn.project_manager.so.project.info;

import com.zkyzn.project_manager.so.file.FileResp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目资源管理器响应体
 * @author Zhang Fan
 */
@Schema(description = "项目资源管理器响应体")
@Data
public class ProjectFolderResp extends FileResp {

    @Schema(description = "文档类型（项目计划/图纸目录/生产会材料/汇报材料/二次统计/合并文档/其他）")
    private String documentType;
}
