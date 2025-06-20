package com.zkyzn.project_manager.models;

import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.zkyzn.project_manager.converts.date.LocalDateConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同节点表实体类
 * @author: Mr-ti
 * Date: 2025/6/20 22:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tab_contract_node")
public class ContractNode {

    @TableId(value = "node_id", type = IdType.AUTO)
    @Schema(description = "合同节点唯一ID")
    private Long nodeId;

    @Schema(description = "关联项目ID", required = true)
    @TableField("project_id")
    private Long projectId;

    @Schema(description = "合同名称", required = true)
    @TableField("contract_name")
    @ExcelProperty("合同名称")
    private String contractName;

    @Schema(description = "项目工号", required = true)
    @TableField("project_number")
    @ExcelProperty("项目工号")
    private String projectNumber;

    @Schema(description = "合同甲方", required = true)
    @TableField("contract_party")
    @ExcelProperty("合同甲方")
    private String contractParty;

    @Schema(description = "计划到款日期", required = true)
    @TableField("planned_payment_date")
    @ExcelProperty(value = "计划到款日期", converter = LocalDateConverter.class)
    private LocalDate plannedPaymentDate;

    @Schema(description = "到款节点名称", required = true)
    @TableField("payment_node_name")
    @ExcelProperty("到款节点名称")
    private String paymentNodeName;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
