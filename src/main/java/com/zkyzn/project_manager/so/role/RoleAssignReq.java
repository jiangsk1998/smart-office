package com.zkyzn.project_manager.so.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 角色分配请求体
 */
@Data
@Schema(description = "角色分配请求体")
public class RoleAssignReq {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "要分配的角色ID列表（如果为空则表示移除所有角色）")
    @JsonProperty("role_ids")
    private Set<Long> roleIds;
}