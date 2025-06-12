package com.zkyzn.project_manager.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户返回体
 * @author: Mr-ti
 * Date: 2025/6/12 13:06
 */

@Schema(description = "用户")
@Data
public class User {

    @Schema(description = "用户ID")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "用户名称")
    @JsonProperty("user_name")
    private String userName;
}
