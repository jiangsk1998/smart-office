package com.zkyzn.project_manager.services;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.MessageInfoDao;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.so.message.MsgReq;
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
        if (req.getReadStatus()!= null){
            queryWrapper.eq(MessageInfo::getReadStatus, req.getReadStatus());
        }
        //Todo 根据接口补充查询条件
        return list(queryWrapper);
    }
}
