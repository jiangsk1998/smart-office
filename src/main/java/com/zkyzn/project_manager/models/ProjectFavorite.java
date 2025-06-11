package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

/**
 * @author: Mr-ti
 * Date: 2025/6/11 13:20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_project_favorite")
@Schema(description = "项目收藏实体")
public class ProjectFavorite {

    @TableId(value = "favorite_id", type = IdType.AUTO)
    @Schema(description = "收藏ID")
    private Long favoriteId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("user_id")
    private Long userId;

    @NotNull(message = "项目ID不能为空")
    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("project_id")
    private Long projectId;

    @Schema(description = "收藏时间")
    @TableField(value = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime createTime;
}
