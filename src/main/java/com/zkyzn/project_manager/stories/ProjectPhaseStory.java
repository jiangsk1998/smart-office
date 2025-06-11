package com.zkyzn.project_manager.stories;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.ProjectPhaseDao;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.message.ChangeNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Todo 状态级联变更 时间级联变更 变更通知
 * Date: 2025/6/10 17:59
 */
@Service
public class ProjectPhaseStory {

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @Resource
    private ProjectPhaseService projectPhaseService;



    public Boolean createPhase(ProjectPhase projectPhase) {
        return this.projectPhaseService.save(projectPhase);
    }

    /**
     * 改变更不产生状态变更
     * @param projectPhase
     * @return
     */
    public Boolean updatePhaseById(ProjectPhase projectPhase) {
        return this.projectPhaseService.updateById(projectPhase);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePhaseStatus(Long id, String status, Long operatorId) {
        // 校验阶段是否存在
        ProjectPhase currentPhase = projectPhaseService.getById(id);
        if (currentPhase == null) {
            return false;
        }

        // 检查状态是否实际变化
        if (status.equals(currentPhase.getPhaseStatus())) {
            // 状态未变化时直接返回成功
            return true;
        }

        //  获取项目信息
        ProjectInfo projectInfo = Optional.ofNullable(projectInfoService.getByProjectId(currentPhase.getProjectId()))
                .orElse(null);
        if (projectInfo == null) {
            return false; // 项目不存在
        }

        String oldStatus = currentPhase.getPhaseStatus();

        // 更新阶段状态
        ProjectPhase updateEntity = new ProjectPhase();
        updateEntity.setPhaseId(id);
        updateEntity.setPhaseStatus(status);
        boolean updateSuccess = projectPhaseService.updateById(updateEntity);

        // TODO 状态检查

        if (!updateSuccess) {
            return false;
        }

        // 构建通知内容
        ChangeNoticeContent content = buildChangeNotice(projectInfo, currentPhase, oldStatus, status);

        // 构建消息对象
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle("项目阶段变更通知");
        messageInfo.setMessageType(1);
        messageInfo.setContent(content);
        messageInfo.setReadStatus(false);

        // 获取通知接收人
        Set<Long> userIdList = getNotifyRecipients(projectInfo);

        // 发送通知
        return messageInfoStory.sendMessages(messageInfo, userIdList);
    }


    private ChangeNoticeContent buildChangeNotice(ProjectInfo projectInfo,
                                                  ProjectPhase phase,
                                                  String oldStatus,
                                                  String newStatus) {
        ChangeNoticeContent content = new ChangeNoticeContent();
        content.setProjectNumber(projectInfo.getProjectNumber());
        content.setProjectName(projectInfo.getProjectName());
        content.setStartDate(projectInfo.getStartDate());
        content.setEndDate(projectInfo.getEndDate());
        content.setCurrentPhase(phase.getPhaseName());
        content.setContent(String.format(
                "项目阶段状态已从 [%s] 更新为: [%s]",
                oldStatus, newStatus
        ));
        return content;
    }

    /**
     * 构建阶段删除通知内容
     */
    private ChangeNoticeContent buildDeleteNotice(ProjectInfo projectInfo, ProjectPhase phase) {
        ChangeNoticeContent content = new ChangeNoticeContent();
        content.setProjectNumber(projectInfo.getProjectNumber());
        content.setProjectName(projectInfo.getProjectName());
        content.setStartDate(projectInfo.getStartDate());
        content.setEndDate(projectInfo.getEndDate());
        content.setCurrentPhase(phase.getPhaseName());
        content.setContent(String.format(
                "项目阶段 [%s] 已被永久删除",
                phase.getPhaseName()
        ));
        return content;
    }


    private Set<Long> getNotifyRecipients(ProjectInfo projectInfo) {
        Set<Long> recipients = new HashSet<>(3);
        Optional.ofNullable(projectInfo.getResponsibleLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getTechnicalLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getPlanSupervisorId()).ifPresent(recipients::add);
        return recipients;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePhaseById(Long id, Long operatorId) {
        // 1. 获取阶段信息
        ProjectPhase phase = projectPhaseService.getById(id);
        if (phase == null) {
            return false; // 阶段不存在
        }

        // 2. 获取关联项目信息
        ProjectInfo projectInfo = Optional.ofNullable(projectInfoService.getByProjectId(phase.getProjectId()))
                .orElse(null);
        if (projectInfo == null) {
            return false; // 项目不存在
        }

        // 3. 构建删除通知内容
        ChangeNoticeContent content = buildDeleteNotice(projectInfo, phase);

        // 4. 构建消息对象
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle("项目阶段删除通知");
        messageInfo.setMessageType(1);
        messageInfo.setContent(content);
        messageInfo.setReadStatus(false);

        // 5. 获取通知接收人（复用现有逻辑）
        Set<Long> userIdList = getNotifyRecipients(projectInfo);

        // 6. 删除阶段
        boolean deleteSuccess = projectPhaseService.lambdaUpdate()
                .eq(ProjectPhase::getPhaseId, id)
                .remove();

        // 7. 删除成功后发送通知
        if (deleteSuccess) {
            messageInfoStory.sendMessages(messageInfo, userIdList);
        }

        return deleteSuccess;
    }

}
