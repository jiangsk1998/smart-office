package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 组织架构部门实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_department")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Department {

    @Schema(description = "部门ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "部门名称")
    @TableField("name")
    private String name;

    @Schema(description = "上级部门ID")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "部门类型")
    @TableField("dept_type")
    private String deptType;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private ZonedDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField("updated_at")
    private ZonedDateTime updatedAt;

    @Schema(description = "下级部门列表")
    @TableField(exist = false) // 告诉MyBatis-Plus，这个字段不是数据库表列
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 如果子部门列表为空，则在JSON中不显示
    private List<Department> children;
}