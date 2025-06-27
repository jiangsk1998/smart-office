package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author Mr-ti
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tab_user_info")
@JsonIgnoreProperties(value = {"userPassword"},allowSetters = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfo {
    @TableId(value = "user_id")
    @Schema(description = "用户Id")
    private Long userId;

    @Schema(description = "用户账号")
    @TableField("user_account")
    private String userAccount;

    @Schema(description = "部门ID")
    @TableField("department_id")
    private Long departmentId;

    @Schema(description = "部门名称")
    @TableField(exist = false)
    private String departmentName;

    @Schema(description = "用户名称")
    @TableField("user_name")
    @NotBlank
    private String userName;

    @Schema(description = "用户密码")
    @TableField("user_password")
    @NotBlank
    private String userPassword;

    @Schema(description = "创建时间")
    @TableField("create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime updateTime;

    @Schema(description = "删除标记")
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete = 0;
}
