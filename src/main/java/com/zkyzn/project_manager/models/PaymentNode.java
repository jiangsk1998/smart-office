package com.zkyzn.project_manager.models;

import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.zkyzn.project_manager.enums.InvoiceStatusEnum;
import com.zkyzn.project_manager.enums.PaymentStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目付款节点表实体类
 * @author: Mr-ti
 * Date: 2025/6/21 00:02
 */
@Data
@TableName("tab_payment_node")
public class PaymentNode {

    @TableId(value = "payment_id", type = IdType.AUTO)
    @Schema(description = "付款节点唯一ID")
    private Long paymentId;

    @Schema(description = "关联项目ID", required = true)
    @TableField("project_id")
    private Long projectId;

    @Schema(description = "项目工号", required = true)
    @TableField("project_number")
    @ExcelProperty("项目工号")
    private String projectNumber;

    @Schema(description = "到款节点名称", required = true)
    @TableField("payment_node_name")
    @ExcelProperty("到款节点名称")
    private String paymentNodeName;

    @Schema(description = "应收款（万元）", required = true)
    @TableField("receivable")
    @ExcelProperty("应收款（万元）")
    private BigDecimal receivable;

    @Schema(description = "部室主管")
    @TableField("department_director")
    @ExcelProperty("部室主管")
    private String departmentDirector;

    @Schema(description = "开票状态", defaultValue = "PENDING")
    @TableField("invoice_status")
    @ExcelProperty("节点开票状态")
    private String invoiceStatus = InvoiceStatusEnum.PENDING.getCode();

    @Schema(description = "到款状态", defaultValue = "PENDING")
    @TableField("payment_status")
    @ExcelProperty("节点到款状态")
    private String paymentStatus = PaymentStatusEnum.PENDING.getCode();

    @Schema(description = "分管科长")
    @TableField("section_chief")
    @ExcelProperty("分管科长")
    private String sectionChief;

    @Schema(description = "分管部领导")
    @TableField("department_leader")
    @ExcelProperty("分管部领导")
    private String departmentLeader;

    @Schema(description = "甲方干系人")
    @TableField("client_stakeholder")
    @ExcelProperty("甲方干系人")
    private String clientStakeholder;

    @Schema(description = "联系方式")
    @TableField("contact_info")
    @ExcelProperty("联系方式")
    private String contactInfo;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
