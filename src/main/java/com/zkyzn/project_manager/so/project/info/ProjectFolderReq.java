package com.zkyzn.project_manager.so.project.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目资源管理器请求体
 * @author Zhang Fan
 */
@Schema(description = "项目资源管理器请求体")
@Data
public class ProjectFolderReq {

    @Schema(description = "文档类型（项目计划/图纸目录/生产会材料/汇报材料/二次统计/合并文档/其他）",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"项目计划", "图纸目录", "生产会材料", "汇报材料", "二次统计", "合并文档", "其他"})
    private String documentType;
}
