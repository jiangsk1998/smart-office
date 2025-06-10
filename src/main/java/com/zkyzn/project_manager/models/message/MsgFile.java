package com.zkyzn.project_manager.models.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件相关信息返回体
 */
@Schema(description = "消息文件")
@Data
public class MsgFile {
    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    @JsonProperty("file_name")
    private String fileName;

    /**
     * 是否为文件目录
     */
    @Schema(description = "是否为文件目录")
    @JsonProperty("is_directory")
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
