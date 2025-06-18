package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_role")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "角色实体")
public class Role {

    @Schema(description = "角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("role_name")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码（唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("role_code")
    private String roleCode;

    @Schema(description = "角色描述")
    @TableField("description")
    private String description;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private ZonedDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private ZonedDateTime updateTime;
}