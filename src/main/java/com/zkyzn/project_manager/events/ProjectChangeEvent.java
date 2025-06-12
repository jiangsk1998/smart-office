package com.zkyzn.project_manager.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author J    iangsk
 */
@Getter
public abstract class ProjectChangeEvent extends ApplicationEvent {

    private final Long operatorId;
    private final String changeType; // e.g., "CREATE", "UPDATE", "DELETE","STATUS_CHANGE"

    /**
     * @param source 事件对象
     * @param operatorId  操作人ID
     * @param changeType  事件类型
     */
    public ProjectChangeEvent(Object source, Long operatorId, String changeType) {
        super(source);
        this.operatorId = operatorId;
        this.changeType = changeType;
    }
}