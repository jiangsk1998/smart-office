package com.zkyzn.project_manager.so.project_info;

import com.zkyzn.project_manager.models.ProjectDocument;
import com.zkyzn.project_manager.models.ProjectInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 项目详情响应体
 */
@Schema(description = "项目详情响应")
@Data
public class ProjectInfoResp extends ProjectInfo {

    /**
     * 项目文档列表
     */
    @Schema(description = "项目文档列表")
    private List<ProjectDocument> projectDocumentList;
}