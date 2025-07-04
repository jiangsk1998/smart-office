package com.zkyzn.project_manager.services;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.OperationLogDao;
import com.zkyzn.project_manager.models.OperationLog;
import com.zkyzn.project_manager.utils.IpUtil;
import com.zkyzn.project_manager.utils.JsonUtil;
import com.zkyzn.project_manager.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class OperationLogService extends MPJBaseServiceImpl<OperationLogDao, OperationLog> {


    private final HttpServletRequest request;

    public OperationLogService(HttpServletRequest request) {
        this.request = request;
    }

    public void logOperation(String operateType, String operateTarget,
                             Long targetId, String operateDetail,
                             Long projectId, Object original, Object updated) {

        // 获取当前用户信息
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserName = SecurityUtil.getCurrentUserName();

        // 创建日志对象
        OperationLog log = new OperationLog();
        log.setOperatorId(currentUserId);
        log.setOperatorName(currentUserName);
        log.setOperateTime(LocalDate.now());
        log.setOperateType(operateType);
        log.setOperateTarget(operateTarget);
        log.setTargetId(targetId);
        log.setOperateDetail(operateDetail);
        log.setProjectId(projectId);
        log.setOriginalData(JsonUtil.toJson(original));
        log.setNewData(JsonUtil.toJson(updated));
        log.setIpAddress(IpUtil.getClientIp(request));

        this.save(log);
    }

    public List<OperationLog> getPlansByPhase(Long projectId) {
        MPJLambdaQueryWrapper<OperationLog> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(OperationLog.class)
                .eq(OperationLog::getProjectId, projectId)
                .orderByAsc(OperationLog::getOperateTime);

        return baseMapper.selectList(wrapper);
    }

}