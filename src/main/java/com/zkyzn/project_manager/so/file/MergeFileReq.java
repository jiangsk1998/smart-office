package com.zkyzn.project_manager.so.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文件合并请求结构
 */
@Schema(description = "文件合并请求结构")
@Data
public class MergeFileReq {
    /**
     * 需要合并的文件列表
     */
    @Schema(description = "需要合并的文件列表")
    List<String> files;
}
