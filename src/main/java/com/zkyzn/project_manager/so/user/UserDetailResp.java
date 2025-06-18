package com.zkyzn.project_manager.so.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.models.Role; // 引入 Role 实体
import com.zkyzn.project_manager.models.UserInfo; // 引入 UserInfo 实体
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 用户详情响应体
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 UserInfo 的字段
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "用户详情响应体")
public class UserDetailResp extends UserInfo {

    @Schema(description = "部门名称")
    @JsonProperty("department_name")
    private String departmentName;

    @Schema(description = "用户角色列表")
    @JsonProperty("roles")
    private List<Role> roles;

    // 由于继承了 UserInfo，所以 UserInfo 的所有字段都会包含在内
    // 例如：userId, username, email, phone, status, createTime, updateTime, departmentId
}