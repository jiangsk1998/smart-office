package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.models.message.BaseContent;
import com.zkyzn.project_manager.models.message.MsgFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 消息信息实体
 *
 * @author jiangsk
 */
@Schema(name = "MessageInfo", description = "消息信息实体")
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tab_message_info", autoResultMap = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageInfo {

    @Schema(description = "主键ID")
    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;


    @Schema(description = "发送者用户ID（字符串）")
    @TableField("sender_id")
//    @NotBlank(message = "发送者用户ID不能为空")
    private Long senderId;


    @Schema(description = "接收者用户ID（字符串）")
    @TableField("receiver_id")
//    @NotBlank(message = "接收者用户ID不能为空")
    private Long receiverId;


    @Schema(description = "消息标题")
    @TableField("title")
//    @NotBlank(message = "消息标题不能为空")
    private String title;


    @Schema(description = "消息内容（JSON结构）")
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private BaseContent content;


    @Schema(description = "消息类型：0=附件通知，1=变更通知，2=即将到期通知，3=延期通知,4=延期反馈，5=延期风险告警,6=定时报告")
    @TableField("message_type")
    @NotBlank(message = "消息类型不能为空")
    private Integer messageType;


    @Schema(description = "阅读状态")
    @TableField("read_status")
    private Boolean readStatus = false;


    @Schema(description = "是否置顶：0=否，1=是")
    @TableField("is_top")
    private Boolean isTop = false;


    @Schema(description = "是否有附件：0=无，1=有")
    @TableField("has_attachment")
    private Boolean hasAttachment;


    @Schema(description = "附件")
    @TableField(value = "attachment", typeHandler = JacksonTypeHandler.class)
    private List<MsgFile> attachment;


    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createTime;


    @Schema(description = "阅读时间")
    @TableField("read_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime readTime;


    @Schema(description = "最后更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime updateTime;


    @Schema(description = "逻辑删除标志：0=正常，1=删除")
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted = false;

    @Schema(description = "是否需要回复：0=否，1=是")
    @TableField("is_reply_required")
    private Boolean isReplyRequired = false;
}