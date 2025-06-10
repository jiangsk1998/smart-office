package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author Mr-ti
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_project_document")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectDocument {

    @Schema(description = "文档唯一ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableId(value = "project_document_id", type = IdType.AUTO)
    private Long projectDocumentId;

    @NotBlank(message = "项目ID不能为空")
    @Schema(description = "关联项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_id")
    private Long projectId;

    @NotBlank(message = "文档类型不能为空")
    @Schema(description = "文档类型（项目计划/图纸目录/生产会材料/汇报材料/二次统计/合并文档/其他）",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"项目计划", "图纸目录", "生产会材料", "汇报材料", "二次统计", "合并文档", "其他"})
    @TableField("document_type")
    private String documentType;

    @NotBlank(message = "文档名称不能为空")
    @Schema(description = "文档名称，文档名称需要以文档类型为结尾，如[某某项目计划.excel]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("document_name")
    private String documentName;

    @NotBlank(message = "文件存储路径不能为空")
    @Schema(description = "文件存储路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("file_path")
    private String filePath;

    @Schema(description = "上传人Id")
    @TableField("uploader_id")
    private String uploaderId;

    @Schema(description = "上传人姓名")
    @TableField("uploader_name")
    private String uploaderName;

    @Schema(description = "上传时间")
    @TableField(value = "upload_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;

    @Schema(description = "文档版本")
    @TableField("version")
    private String version;

    @Schema(description = "文档描述")
    @TableField("description")
    private String description;

    @Schema(description = "是否最新版本", defaultValue = "true")
    @TableField("is_latest")
    private Boolean isLatest = true;
}