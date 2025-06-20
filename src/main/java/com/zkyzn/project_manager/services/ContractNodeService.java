package com.zkyzn.project_manager.services;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.converts.imports.ContractNodeExcel;
import com.zkyzn.project_manager.mappers.ContractNodeDao;
import com.zkyzn.project_manager.models.ContractNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Mr-ti
 * Date: 2025/6/20 23:09
 */
@Service
public class ContractNodeService extends MPJBaseServiceImpl<ContractNodeDao, ContractNode> {

    public boolean saveBatchContractNodes(List<? extends ContractNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        // 转换为 ContractNode 列表
        List<ContractNode> contractNodes = new ArrayList<>(nodes);
        return saveBatch(contractNodes);
    }

    /**
     * 批量保存 Excel 导入的合同节点
     * @param excelNodes Excel 导入的合同节点列表
     * @return 是否保存成功
     */
    public boolean saveBatchProjectContractExcel(List<ContractNodeExcel> excelNodes) {
        // 1. 过滤掉 Excel 中的额外字段
        List<ContractNode> contractNodes = excelNodes.stream()
                .map(excel -> {
                    ContractNode node = new ContractNode();
                    // 复制所有 ContractNode 字段
                    node.setProjectId(excel.getProjectId());
                    node.setContractName(excel.getContractName());
                    node.setProjectNumber(excel.getProjectNumber());
                    node.setContractParty(excel.getContractParty());
                    node.setPlannedPaymentDate(excel.getPlannedPaymentDate());
                    node.setPaymentNodeName(excel.getPaymentNodeName());
                    return node;
                })
                .collect(Collectors.toList());

        // 2. 批量保存
        return saveBatchContractNodes(contractNodes);
    }
}
