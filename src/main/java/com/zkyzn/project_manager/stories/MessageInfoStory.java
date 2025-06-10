package com.zkyzn.project_manager.stories;

import cn.hutool.core.bean.BeanUtil;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.services.MessageInfoService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.docx4j.com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;


/**
 * @author Jiangsk
 */
@Service
public class MessageInfoStory {

    @Resource
    private MessageInfoService messageInfoService;

    private static final Logger log = LoggerFactory.getLogger(MessageInfoStory.class);

    /**
     * 批量发送消息给多个用户
     *
     * @param message    消息模板（不包含接收者ID）
     * @param userIdList 接收用户列表
     * @return true=全部发送成功, false=全部失败
     */
    public boolean sendMessages(MessageInfo message, Set<Long> userIdList) {
        // 参数校验防御性编程
        if (message == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(userIdList)) {
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
        // 参数校验防御性编程
        if (message == null) {
            log.warn("消息内容不能为空");
            return false;
        }
        if (message.getReceiverId() == null) {
            log.warn("接收用户不能为空");
            return false;
        }
        return this.messageInfoService.save(message);
    }

    public Boolean postSendFeedbackMessage(MessageInfo messageInfo) {
        Set<Long> userIdList = getUserIdListByMessage(messageInfo);
        return this.sendMessages(messageInfo, userIdList);
    }

    private Set<Long> getUserIdListByMessage(MessageInfo message) {
        // Todo 获取所有接收者ID
        return Sets.newHashSet();
    }
}
