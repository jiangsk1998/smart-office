package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Jiangsk
 */
@Data
@TableName("tab_operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("operate_time")
    private LocalDate operateTime;

    @TableField("operate_type")
    private String operateType;

    @TableField("operate_target")
    private String operateTarget;

    @TableField("target_id")
    private Long targetId;

    @TableField("operate_detail")
    private String operateDetail;

    @TableField("project_id")
    private Long projectId;

    @TableField("original_data")
    private String originalData;

    @TableField("new_data")
    private String newData;

    @TableField("ip_address")
    private String ipAddress;
}