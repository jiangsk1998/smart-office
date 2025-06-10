package com.zkyzn.project_manager.models;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zkyzn.project_manager.so.file.FileResp;
import com.zkyzn.project_manager.so.message.BaseContent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    @TableId(value = "message_id", type = IdType.INPUT)
//    @JsonProperty("messageId")
//    @NotBlank(message = "主键ID不能为空")
    private Long messageId;


    @Schema(description = "发送者用户ID（字符串）")
    @TableField("sender_id")
//    @JsonProperty("senderId")
    @NotBlank(message = "发送者用户ID不能为空")
    private String senderId;


    @Schema(description = "接收者用户ID（字符串）")
    @TableField("receiver_id")
//    @JsonProperty("receiverId")
    @NotBlank(message = "接收者用户ID不能为空")
    private String receiverId;


    @Schema(description = "消息标题")
    @TableField("title")
    @NotBlank(message = "消息标题不能为空")
    private String title;


    @Schema(description = "消息内容（JSON结构）")
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private BaseContent content;


    @Schema(description = "消息类型：0=附件通知，1=变更通知，2=即将到期通知，3=延期通知,4=延期反馈，5=延期风险告警")
    @TableField("message_type")
//    @JsonProperty("messageType")
    @NotBlank(message = "消息类型不能为空")
    private Integer messageType;


    @Schema(description = "阅读状态")
    @TableField("read_status")
//    @JsonProperty("readStatus")
    private Boolean readStatus = false;


    @Schema(description = "是否置顶：0=否，1=是")
    @TableField("is_top")
//    @JsonProperty("isTop")
    private Boolean isTop = false;


    @Schema(description = "是否有附件：0=无，1=有")
    @TableField("has_attachment")
//    @JsonProperty("hasAttachment")
    private Boolean hasAttachment;


    @Schema(description = "附件")
    @TableField(value = "attachment", typeHandler = JacksonTypeHandler.class)
    private List<FileResp> attachment;


    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
//    @JsonProperty("createTime")
    private LocalDateTime createTime;


    @Schema(description = "阅读时间")
    @TableField("read_time")
//    @JsonProperty("readTime")
    private LocalDateTime readTime;


    @Schema(description = "最后更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
//    @JsonProperty("updateTime")
    private LocalDateTime updateTime;


    @Schema(description = "逻辑删除标志：0=正常，1=删除")
    @TableField("is_deleted")
    @TableLogic
//    @JsonProperty("isDeleted")
    private Integer isDeleted = 0;
}