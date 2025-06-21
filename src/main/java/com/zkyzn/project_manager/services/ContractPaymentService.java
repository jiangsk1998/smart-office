package com.zkyzn.project_manager.services;

import com.zkyzn.project_manager.mappers.ContractPaymentDao;
import com.zkyzn.project_manager.so.project.ai.ContractPaymentWideDTO;
import org.springframework.stereotype.Service;
/**
 * @author: Mr-ti
 * Date: 2025/6/21 11:58
 */


import java.util.List;

@Service
public class ContractPaymentService {

    private final ContractPaymentDao contractPaymentDao;

    public ContractPaymentService(ContractPaymentDao contractPaymentDao) {
        this.contractPaymentDao = contractPaymentDao;
    }

    /**
     * 获取合同与付款节点合并宽表数据
     */
    public List<ContractPaymentWideDTO> getContractPaymentWideData() {
        return contractPaymentDao.getContractPaymentWideData();
    }
}
