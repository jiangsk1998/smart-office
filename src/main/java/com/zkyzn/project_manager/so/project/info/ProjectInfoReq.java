package com.zkyzn.project_manager.so.project.info;

import com.zkyzn.project_manager.models.ProjectInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 创建项目请求体
 * @author Mr-ti
 */
@Schema(description = "项目信息请求")
@Data
public class ProjectInfoReq extends ProjectInfo {

    /**
     * 项目文档列表
     */
    @Schema(description = "项目文档列表")
    private List<ProjectDocumentReq> projectDocumentList;
}