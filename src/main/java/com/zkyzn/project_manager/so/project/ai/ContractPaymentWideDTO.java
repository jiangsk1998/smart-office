package com.zkyzn.project_manager.so.project.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: Mr-ti
 * Date: 2025/6/21 11:55
 */
@Data
@Schema(description = "合同与付款节点合并宽表")
public class ContractPaymentWideDTO {

    // 合同节点字段
    @Schema(description = "合同节点ID")
    private Long contractNodeId;

    @Schema(description = "关联项目ID")
    private Long projectId;

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "项目工号")
    private String projectNumber;

    @Schema(description = "合同甲方")
    private String contractParty;

    @Schema(description = "计划到款日期")
    private LocalDate plannedPaymentDate;

    @Schema(description = "到款节点名称")
    private String paymentNodeName;

    @Schema(description = "合同节点创建时间")
    private LocalDateTime contractCreateTime;

    @Schema(description = "合同节点更新时间")
    private LocalDateTime contractUpdateTime;

    // 付款节点字段
    @Schema(description = "付款节点ID")
    private Long paymentId;

    @Schema(description = "应收款（万元）")
    private BigDecimal receivable;

    @Schema(description = "部室主管")
    private String departmentDirector;

    @Schema(description = "开票状态")
    private String invoiceStatus;

    @Schema(description = "到款状态")
    private String paymentStatus;

    @Schema(description = "分管科长")
    private String sectionChief;

    @Schema(description = "分管部领导")
    private String departmentLeader;

    @Schema(description = "甲方干系人")
    private String clientStakeholder;

    @Schema(description = "联系方式")
    private String contactInfo;

    @Schema(description = "付款节点创建时间")
    private LocalDateTime paymentCreateTime;

    @Schema(description = "付款节点更新时间")
    private LocalDateTime paymentUpdateTime;
}
