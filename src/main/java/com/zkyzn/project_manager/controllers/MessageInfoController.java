package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.services.MessageInfoService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.message.MsgReq;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jiangsk
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "api/messages", description = "消息盒子")
public class MessageInfoController {

    @Autowired
    private MessageInfoService messageInfoService;

    @PostMapping
    @Operation(summary = "创建消息")
    public Result<MessageInfo> create(@RequestBody MessageInfo messageInfo) {
        boolean success = messageInfoService.save(messageInfo);
        return success ? ResUtil.ok(messageInfo) : ResUtil.fail("创建失败");
    }

    @PatchMapping("/{id}/read-status") // 改为PATCH部分更新
    @Operation(summary = "标记已读")
    public Result<Boolean> markAsRead(@PathVariable String id) {
        return ResUtil.ok(messageInfoService.read(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息")
    public Result<Boolean> delete(@PathVariable String id) {
        return ResUtil.ok(messageInfoService.removeById(id));
    }

    @PostMapping("/query")  // 改为 POST 方法接收复杂查询条件
    @Operation(summary = "消息列表（条件查询）")
    public ResultList<MessageInfo> queryMessages(@RequestBody MsgReq req) {
        return ResUtil.list(messageInfoService.listByUserId(req));
    }
}