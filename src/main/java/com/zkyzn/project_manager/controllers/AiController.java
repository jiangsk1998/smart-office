package com.zkyzn.project_manager.controllers;


import com.zkyzn.project_manager.services.ContractPaymentService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.project.ai.ContractPaymentWideDTO;
import com.zkyzn.project_manager.so.project.ai.MonthlyPlansDTO;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/21 12:07
 */
@RestController
@Tag(name = "api/v1/ai", description = "项目看板管理")
@RequestMapping("api/v1/ai")
public class AiController {


    @Resource
    private ContractPaymentService contractPaymentService;
    @Resource
    private ProjectPlanService projectPlanService;

    @Operation(summary = "获取合同与付款节点合并宽表数据")
    @GetMapping("/contract-payment")
    public Result<List<ContractPaymentWideDTO>> getContractPaymentWideData() {
        return ResUtil.ok(contractPaymentService.getContractPaymentWideData());
    }

    @Operation(summary = "获取本月和下个月的工作计划")
    @GetMapping("/monthly-plans")
    public Result<MonthlyPlansDTO> getMonthlyPlans() {
        return ResUtil.ok(projectPlanService.getMonthlyPlans());
    }
}
