package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * Copyright(C) 2024 HFHX.All right reserved.
 * ClassName: ProjectDocument
 * Description: TODO
 * Version: 1.0
 * Author: Mr-ti
 * Date: 2025/6/7 20:55
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
    @Schema(description = "文档名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("document_name")
    private String documentName;

    @NotBlank(message = "文件存储路径不能为空")
    @Schema(description = "文件存储路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("file_path")
    private String filePath;

    @NotNull(message = "文件大小不能为空")
    @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("file_size")
    private Long fileSize;

    @NotBlank(message = "上传人不能为空")
    @Schema(description = "上传人", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("uploader")
    private String uploader;

    @Schema(description = "上传时间")
    @TableField(value = "upload_time", fill = FieldFill.INSERT)
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