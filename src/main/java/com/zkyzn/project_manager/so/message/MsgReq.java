package com.zkyzn.project_manager.so.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "查询消息请求")
public class MsgReq {

    @Schema(description = "用户Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "排序条件：createTime,readTime,updateTime")
    private List<SortCondition> sorts;

    @Schema(description = "消息类型：1=通知，2=告警，3=附件")
    @TableField("message_type")
    @JsonProperty("messageType")
    private Integer messageType;

    @Schema(description = "阅读状态：0=未读，1=已读")
    @TableField("read_status")
    @JsonProperty("readStatus")
    private Integer readStatus;

    @Schema(description = "关键词模糊搜索")
    private String keyword;

    /**
     * 排序条件嵌套类
     */
    @Data
    public static class SortCondition {
        private String field;      // 排序字段名（如："createTime"）
        private boolean asc = true; // 排序方向（默认升序）
    }
}
