package com.zkyzn.project_manager.services;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.converts.imports.PaymentNodeExcel;
import com.zkyzn.project_manager.mappers.PaymentNodeDao;
import com.zkyzn.project_manager.models.PaymentNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Mr-ti
 * Date: 2025/6/21 00:10
 */
@Service
public class PaymentNodeService extends MPJBaseServiceImpl<PaymentNodeDao, PaymentNode> {

    public boolean saveBatchPaymentNodes(List<? extends PaymentNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        // 转换为 ContractNode 列表
        List<PaymentNode> paymentNodes = new ArrayList<>(nodes);
        return saveBatch(paymentNodes);
    }

    /**
     * 批量保存 Excel 导入的款项节点
     * @param paymentNodes Excel 导入的款项节点列表
     * @return 是否保存成功
     */
    public boolean saveBatchProjectPaymentExcel(List<PaymentNodeExcel> paymentNodes) {
        // 1. 过滤掉 Excel 中的额外字段
        List<PaymentNode> paymentNodeList = paymentNodes.stream()
                .map(excel -> {
                    PaymentNode node = new PaymentNode();
                    // 复制所有 ContractNode 字段
                    node.setProjectId(excel.getProjectId());
                    node.setPaymentNodeName(excel.getPaymentNodeName());
                    node.setProjectNumber(excel.getProjectNumber());
                    node.setReceivable(excel.getReceivable());
                    node.setDepartmentDirector(excel.getDepartmentDirector());
                    node.setInvoiceStatus(excel.getInvoiceStatus());
                    node.setPaymentStatus(excel.getPaymentStatus());
                    node.setSectionChief(excel.getSectionChief());
                    node.setDepartmentLeader(excel.getDepartmentLeader());
                    node.setClientStakeholder(excel.getClientStakeholder());
                    node.setContactInfo(excel.getContactInfo());
                    return node;
                })
                .collect(Collectors.toList());

        // 2. 批量保存
        return saveBatchPaymentNodes(paymentNodeList);
    }

    public List<PaymentNode> findByStatusIn(List<String> statuses) {
        MPJLambdaQueryWrapper<PaymentNode> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.in(PaymentNode::getPaymentStatus, statuses);
        return baseMapper.selectList(wrapper);
    }

    public List<PaymentNode> findByProjectId(Long projectId) {
        MPJLambdaQueryWrapper<PaymentNode> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.eq(PaymentNode::getProjectId, projectId);
        return baseMapper.selectList(wrapper);
    }

    public List<PaymentNode> findByProjectNumber(String projectNumber) {
        MPJLambdaQueryWrapper<PaymentNode> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.eq(PaymentNode::getProjectNumber, projectNumber);
        return baseMapper.selectList(wrapper);
    }
}