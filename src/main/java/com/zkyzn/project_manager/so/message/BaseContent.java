package com.zkyzn.project_manager.so.message;

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
        @JsonSubTypes.Type(value = ChangeNotice.class, name = "notice"), // 变更提醒
        @JsonSubTypes.Type(value = DueDateNotice.class, name = "dueDate"), //即将到期提醒
        @JsonSubTypes.Type(value = DelayAlert.class, name = "delay"), //延期风险告警
})
public class BaseContent {
}
