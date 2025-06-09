package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.MessageInfoDao;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.so.message.MsgReq;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageInfoService extends MPJBaseServiceImpl<MessageInfoDao, MessageInfo> {


    /**
     * 根据消息Id修改消息状态为已读
     *
     * @param messageInfoId 消息Id
     * @return
     */
    public boolean read(String messageInfoId) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageId(messageInfoId);
        messageInfo.setReadStatus(1);
        //Todo 只能已读当前会话用户的消息
        return updateById(messageInfo);
    }

    public List<MessageInfo> listByUserId(MsgReq req) {
        // 1. 使用单表查询Wrapper
        LambdaQueryWrapper<MessageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageInfo::getReceiverId, req.getUserId())
                .eq(MessageInfo::getIsDeleted, 0)
                .eq(req.getReadStatus() != null, MessageInfo::getReadStatus, req.getReadStatus())
                .eq(req.getMessageType() != null, MessageInfo::getMessageType, req.getMessageType());

        // 2. 关键词搜索（安全OR条件）
        if (StringUtils.isNotBlank(req.getKeyword())) {
            queryWrapper.and(w -> w
                    .like(MessageInfo::getTitle, req.getKeyword())
                    .or()
                    .like(MessageInfo::getContent, req.getKeyword())
            );
        }

        // 3. 动态排序（字段白名单）
        if (CollectionUtils.isNotEmpty(req.getSorts())) {
            req.getSorts().forEach(sort -> {
                SFunction<MessageInfo, ?> field = getSortField(sort.getField());
                queryWrapper.orderBy(true, sort.isAsc(), field);
            });
        } else {
            queryWrapper.orderByDesc(MessageInfo::getCreateTime);
        }

        // 4. 返回结果
        return list(queryWrapper);
    }

    /**
     * 字段名映射方法（防止SQL注入）
     *
     * @param fieldName 字段名
     * @return
     */
    private SFunction<MessageInfo, ?> getSortField(String fieldName) {
        switch (fieldName) {
            case "createTime":
                return MessageInfo::getCreateTime;
            case "updateTime":
                return MessageInfo::getUpdateTime;
            case "readTime":
                return MessageInfo::getReadTime;
            default:
                throw new IllegalArgumentException("无效排序字段");
        }
    }
}
