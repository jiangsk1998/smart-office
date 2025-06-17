package com.zkyzn.project_manager.models.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Jiangsk
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChangeNoticeContent.class, name = "changeNotice"), // 变更提醒
        @JsonSubTypes.Type(value = DueDateNoticeContent.class, name = "dueDateNotice"), // 即将到期提醒
        @JsonSubTypes.Type(value = DelayNoticeContent.class, name = "delayNotice"), // 延期原因填写通知
        @JsonSubTypes.Type(value = DelayFeedbackContent.class, name = "delayFeedback"), // 超期反馈表单发送
        @JsonSubTypes.Type(value = DelayAlertContent.class, name = "delayAlert"), // 延期风险告警
})
public class BaseContent {
}
