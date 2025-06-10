package com.zkyzn.project_manager.so.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zkyzn.project_manager.so.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "用户Id不能为空")
    private Long userId;

    @Schema(description = "排序条件：createTime,readTime,updateTime")
    private List<SortCondition> sorts;

    @Schema(description = "消息类型：0=附件通知，1=变更通知，2=即将到期通知，3=延期通知,4=延期反馈，5=延期风险告警")
    private List<Integer> messageTypes;

    @Schema(description = "阅读状态：0=未读，1=已读")
    private Boolean readStatus;

    @Schema(description = "关键词模糊搜索")
    private String keyword;

    @Schema(description = "当前页")
    private Integer pageNo;

    private Integer pageSize;

    /**
     * 排序条件嵌套类
     */
    @Data
    public static class SortCondition {
        private String field;      // 排序字段名（如："createTime"）
        private boolean asc = true; // 排序方向（默认升序）
    }
}
