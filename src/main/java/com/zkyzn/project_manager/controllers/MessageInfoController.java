package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.message.BaseContent;
import com.zkyzn.project_manager.models.message.DelayFeedbackContent;
import com.zkyzn.project_manager.services.MessageInfoService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.message.MsgReq;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.com.google.common.collect.Sets;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jiangsk
 */
@RestController
@RequestMapping("/api/message")
@Tag(name = "api/messages", description = "消息盒子")
public class MessageInfoController {

    @Resource
    private MessageInfoService messageInfoService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @PostMapping
    @Operation(summary = "发送超期反馈")
    public Result<Boolean> postSendFeedbackMessage(@RequestBody MessageInfo messageInfo) {
        messageInfo.setSenderId(1L);

        BaseContent content = messageInfo.getContent();

        if (content instanceof DelayFeedbackContent delayFeedbackContent) {
            // Todo 获取当前超期反馈表消息接收人
            Set<Long> userIdList = Sets.newHashSet();
            userIdList.add(1L);
            return ResUtil.ok(this.messageInfoStory.sendMessages(messageInfo, userIdList));

        } else {
            return ResUtil.fail("只允许发送超期反馈类型消息");

        }
    }


    @PutMapping("/{id}/read/status") // 改为PATCH部分更新
    @Operation(summary = "已读状态变更")
    public Result<Boolean> putReadStatus(@PathVariable Long id, @RequestParam Boolean readStatus) {
        return ResUtil.ok(messageInfoService.updateReadStatusByMessageId(id, readStatus));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息")
    public Result<Boolean> deleteMessageById(@PathVariable String id) {
        return ResUtil.ok(messageInfoService.deleteMessageById(id));
    }

    @GetMapping("/query")  // 改为 POST 方法接收复杂查询条件
    @Operation(summary = "消息列表（分页查询）")
    public ResultList<MessageInfo> getMessagesByPage(
            @RequestParam(required = false) List<Integer> messageTypes,
            @RequestParam(required = false) Boolean readStatus,
            @RequestParam(required = false) String keyword,
            @Parameter(
                    example = "createTime,asc|readTime,desc",
                    description = "排序参数格式: '字段1,asc|字段2,desc'，支持的字段包括: createTime, readTime, updateTime",
                    schema = @Schema(type = "动态排序")
            )
            @RequestParam(required = false, defaultValue = "createTime,asc") String sorts,  // 排序参数格式: "createTime,asc|readTime,desc"
            @RequestParam(required = false, defaultValue = "0") Integer pageNo,   // 分页参数
            @RequestParam(required = false, defaultValue = "50") Integer pageSize) {  // 分页参数

        // 构建查询条件
        MsgReq req = new MsgReq();
        // Todo 获取当前用户
        req.setUserId(1L);
        req.setMessageTypes(messageTypes);
        req.setReadStatus(readStatus);
        req.setKeyword(keyword);
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);

        // 解析排序参数
        if (StringUtils.isNotBlank(sorts)) {
            List<MsgReq.SortCondition> sortList = Arrays.stream(sorts.split("\\|"))
                    .map(sort -> {
                        String[] arr = sort.split(",");
                        MsgReq.SortCondition sc = new MsgReq.SortCondition();
                        sc.setField(arr[0]);
                        sc.setAsc(arr.length > 1 && "asc".equalsIgnoreCase(arr[1]));
                        return sc;
                    })
                    .collect(Collectors.toList());
            req.setSorts(sortList);
        }

        // 执行分页查询
        return ResUtil.list(messageInfoService.getMessageByPage(req));
    }
}