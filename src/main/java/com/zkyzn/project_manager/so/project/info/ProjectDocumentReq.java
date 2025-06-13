package com.zkyzn.project_manager.so.project.info;


import com.zkyzn.project_manager.models.ProjectDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Mr-ti
 * Date: 2025/6/10 09:55
 */
@Schema(description = "创建项目请求--项目文档部分")
@Data
public class ProjectDocumentReq extends ProjectDocument {

    /**
     * 文档操作指令
     */
    @Schema(description = "操作指令 C:创建 U:修改 D:删除， 除其他外，同一个文档类型只能有一个文档，如果没有就是创建，有就是更新或者删除", defaultValue = "C")
    private String operator;
}
