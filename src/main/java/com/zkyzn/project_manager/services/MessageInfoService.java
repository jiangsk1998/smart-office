package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
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
        MPJLambdaQueryWrapper<MessageInfo> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.eq(MessageInfo::getReceiverId, req.getUserId());
        queryWrapper.eq(MessageInfo::getIsDeleted, 0);

        //已读未读
        if (req.getReadStatus() != null) {
            queryWrapper.eq(MessageInfo::getReadStatus, req.getReadStatus());
        }
        //Todo 根据需求补充查询条件

        // 关键词模糊搜索
        if (StringUtils.isNotBlank(req.getKeyword())) {
            queryWrapper.and(w -> w
                    .like(MessageInfo::getTitle, req.getKeyword())
                    .or()
                    .like(MessageInfo::getContent, req.getKeyword())
            );
        }
        //消息类型匹配
        if (null != req.getMessageType()) {
            queryWrapper.eq(MessageInfo::getMessageType, req.getMessageType());
        }

        // 动态排序处理（支持多字段）
        if (CollectionUtils.isNotEmpty(req.getSorts())) {
            req.getSorts().forEach(sort -> {
                if (sort.isAsc()) {
                    queryWrapper.orderByAsc(getSortField(sort.getField())); // 升序
                } else {
                    queryWrapper.orderByDesc(getSortField(sort.getField())); // 降序
                }
            });
        } else {
            queryWrapper.orderByDesc(MessageInfo::getCreateTime); // 默认按创建时间倒排
        }

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
