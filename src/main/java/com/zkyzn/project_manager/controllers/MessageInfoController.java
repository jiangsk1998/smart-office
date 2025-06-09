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

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "消息接口")
public class MessageInfoController {

    @Autowired
    private MessageInfoService messageInfoService;

    @PostMapping
    @Operation(summary = "创建消息")
    public boolean save(@RequestBody MessageInfo messageInfo) {
        return messageInfoService.save(messageInfo);
    }

    @PutMapping("/read/{messageInfoId}")
    @Operation(summary = "消息已读")
    public Result<Boolean> read(@PathVariable String messageInfoId) {
        Boolean read = messageInfoService.read(messageInfoId);
        return ResUtil.ok(read);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息")
    public boolean delete(@PathVariable String id) {
        return messageInfoService.removeById(id);
    }

    @GetMapping
    @Operation(summary = "消息列表")
    public ResultList<MessageInfo> getByUserId(@RequestBody MsgReq req) {
        List<MessageInfo> infoList = messageInfoService.listByUserId(req);
        return ResUtil.list(infoList);
    }

}