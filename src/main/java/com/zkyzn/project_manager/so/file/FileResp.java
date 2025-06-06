package com.zkyzn.project_manager.so.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件相关信息返回体
 */
@Schema(description = "文件相关信息返回体")
@Data
public class FileResp {
    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 是否为文件目录
     */
    @Schema(description = "是否为文件目录")
    private Boolean isDirectory;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long size;

    /**
     * 文件访问地址
     */
    @Schema(description = "文件访问地址")
    private String uri;
}
