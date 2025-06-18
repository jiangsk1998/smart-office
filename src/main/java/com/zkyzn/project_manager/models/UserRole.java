package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

/**
 * 用户角色关系实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_user_role")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "用户角色关系实体")
public class UserRole {

    @Schema(description = "用户角色关系ID")
    @TableId(value = "user_role_id", type = IdType.AUTO)
    private Long userRoleId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("user_id")
    private Long userId;

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private ZonedDateTime createTime;
}