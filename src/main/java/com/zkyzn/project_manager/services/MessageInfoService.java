package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.MessageInfoDao;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.so.PageReq;
import com.zkyzn.project_manager.so.message.MsgReq;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageInfoService extends MPJBaseServiceImpl<MessageInfoDao, MessageInfo> {


    /**
     * 根据消息Id修改消息状态为已读
     *
     * @param messageInfoId 消息Id
     * @return
     */
    public boolean updateReadStatusByMessageId(Long messageInfoId, Boolean readStatus) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageId(messageInfoId);
        messageInfo.setReadStatus(readStatus);
        return updateById(messageInfo);
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

    private String escapeLike(String keyword) {
        return "%" + StringUtils.replaceEach(keyword,
                new String[]{"%", "_"},
                new String[]{"\\%", "\\_"}) + "%";
    }

    public boolean createMessage(MessageInfo messageInfo) {
        return this.save(messageInfo);
    }

    public Boolean deleteMessageById(String id) {
        return this.removeById(id);
    }

    public Page<MessageInfo> getMessageByPage(MsgReq req) {

        // 创建分页对象
        Page<MessageInfo> page = new Page<>(req.getPageNo(), req.getPageSize());

        // 使用单表查询Wrapper
        LambdaQueryWrapper<MessageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageInfo::getReceiverId, req.getUserId())
                .eq(MessageInfo::getIsDeleted, 0)
                .eq(req.getReadStatus() != null, MessageInfo::getReadStatus, req.getReadStatus())
                .in(CollectionUtils.isNotEmpty(req.getMessageTypes()), MessageInfo::getMessageType, req.getMessageTypes());


        // 关键词搜索（安全OR条件）
        if (StringUtils.isNotBlank(req.getKeyword())) {
            String safeKeyword = escapeLike(req.getKeyword());
            queryWrapper.and(w -> w
                    .like(MessageInfo::getTitle, safeKeyword)
                    .or()
                    .like(MessageInfo::getContent, safeKeyword)
            );
        }

        // 动态排序（字段白名单）
        if (CollectionUtils.isNotEmpty(req.getSorts())) {
            req.getSorts().forEach(sort -> {
                SFunction<MessageInfo, ?> field = getSortField(sort.getField());
                queryWrapper.orderBy(true, sort.isAsc(), field);
            });
        } else {
            queryWrapper.orderByDesc(MessageInfo::getCreateTime);
        }

        // 分页查询
        return this.page(page, queryWrapper);
    }
}
