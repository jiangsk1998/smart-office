package com.zkyzn.project_manager.stories;

import cn.hutool.core.bean.BeanUtil;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.User;
import com.zkyzn.project_manager.services.MessageInfoService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Jiangsk
 */
@Service
public class MessageInfoStory {

    @Resource
    private MessageInfoService messageInfoService;

    @Resource
    private ProjectInfoService projectInfoService;

    /**
     * 批量发送消息给多个用户
     *
     * @param message    消息模板（不包含接收者ID）
     * @param userIdList 接收用户列表
     * @return true=全部发送成功, false=全部失败
     */
    public boolean sendMessages(MessageInfo message, Set<Long> userIdList) {
        // 参数校验
        if (message == null || CollectionUtils.isEmpty(userIdList)) {
            return false;
        }

        // 批量创建消息副本
        List<MessageInfo> messagesToSend = new ArrayList<>(userIdList.size());
        for (Long userId : userIdList) {
            messagesToSend.add(createMessageCopy(message, userId));
        }
        // 批量发送
        return messageInfoService.saveBatch(messagesToSend);
    }

    /**
     * 发送超期反馈消息给项目主管
     * @param message
     * @param projectNumber
     * @return
     */
    public boolean postSendFeedbackMessage(MessageInfo message, String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        List<User> userList = projectInfo.getPlanSupervisors();
        Set<Long> userIdSet = userList.stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());
        message.setTitle("超期反馈");
        return sendMessages(message, userIdSet);
    }

    /**
     * 创建消息副本并设置接收者
     *
     * @param template 消息模板
     * @param userId   接收用户
     * @return 新消息对象
     */
    private MessageInfo createMessageCopy(MessageInfo template, Long userId) {
        // 创建深度拷贝防止原始对象被修改
        MessageInfo copy = new MessageInfo();
        BeanUtil.copyProperties(template, copy);
        //消息ID为Null
        copy.setMessageId(null);

        // 设置接收者ID
        copy.setReceiverId(userId);

        // 设置消息创建时间
        copy.setCreateTime(ZonedDateTime.now());

        // 确保初始状态为未读
        copy.setReadStatus(false);

        return copy;
    }

    /**
     * 发送消息给用户
     *
     * @param message 消息模板（包含接收者ID）
     * @return true=成功, false=失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessage(MessageInfo message) {
        // 参数校验
        if (message == null || message.getReceiverId() == null) {
            return false;
        }
        message.setCreateTime(ZonedDateTime.now());
        return this.messageInfoService.save(message);
    }


}
