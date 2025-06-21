package com.zkyzn.project_manager.mappers;

import com.zkyzn.project_manager.so.project.ai.ContractPaymentWideDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * @author: Mr-ti
 * Date: 2025/6/21 11:56
 */
@Mapper
public interface ContractPaymentDao {

    @Select("SELECT " +
            "c.node_id AS contractNodeId, " +
            "c.project_id AS projectId, " +
            "c.contract_name AS contractName, " +
            "c.project_number AS projectNumber, " +
            "c.contract_party AS contractParty, " +
            "c.planned_payment_date AS plannedPaymentDate, " +
            "c.payment_node_name AS paymentNodeName, " +
            "c.create_time AS contractCreateTime, " +
            "c.update_time AS contractUpdateTime, " +
            "p.payment_id AS paymentId, " +
            "p.receivable AS receivable, " +
            "p.department_director AS departmentDirector, " +
            "p.invoice_status AS invoiceStatus, " +
            "p.payment_status AS paymentStatus, " +
            "p.section_chief AS sectionChief, " +
            "p.department_leader AS departmentLeader, " +
            "p.client_stakeholder AS clientStakeholder, " +
            "p.contact_info AS contactInfo, " +
            "p.create_time AS paymentCreateTime, " +
            "p.update_time AS paymentUpdateTime " +
            "FROM tab_contract_node c " +
            "LEFT JOIN tab_payment_node p " +
            "ON c.project_number = p.project_number " +
            "AND c.payment_node_name = p.payment_node_name " +
            "UNION " +
            "SELECT " +
            "c.node_id AS contractNodeId, " +
            "p.project_id AS projectId, " +
            "c.contract_name AS contractName, " +
            "p.project_number AS projectNumber, " +
            "c.contract_party AS contractParty, " +
            "c.planned_payment_date AS plannedPaymentDate, " +
            "p.payment_node_name AS paymentNodeName, " +
            "c.create_time AS contractCreateTime, " +
            "c.update_time AS contractUpdateTime, " +
            "p.payment_id AS paymentId, " +
            "p.receivable AS receivable, " +
            "p.department_director AS departmentDirector, " +
            "p.invoice_status AS invoiceStatus, " +
            "p.payment_status AS paymentStatus, " +
            "p.section_chief AS sectionChief, " +
            "p.department_leader AS departmentLeader, " +
            "p.client_stakeholder AS clientStakeholder, " +
            "p.contact_info AS contactInfo, " +
            "p.create_time AS paymentCreateTime, " +
            "p.update_time AS paymentUpdateTime " +
            "FROM tab_payment_node p " +
            "LEFT JOIN tab_contract_node c " +
            "ON p.project_number = c.project_number " +
            "AND p.payment_node_name = c.payment_node_name " +
            "WHERE c.node_id IS NULL")
    List<ContractPaymentWideDTO> getContractPaymentWideData();
}