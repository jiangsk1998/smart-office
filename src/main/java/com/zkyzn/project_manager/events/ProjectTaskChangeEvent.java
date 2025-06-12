package com.zkyzn.project_manager.events;

import com.zkyzn.project_manager.models.ProjectPlan;
import lombok.Getter;

@Getter
public class ProjectTaskChangeEvent extends ProjectChangeEvent {

    private final ProjectPlan originalPlan; // 存储变更前的数据
    private final ProjectPlan updatedPlan;  // 存储变更后的数据

    /**
     * @param source The object on which the event initially occurred (can be 'this').
     * @param operatorId The ID of the user who triggered the event.
     * @param changeType The type of change (e.g., "CREATE", "UPDATE", "DELETE").
     * @param originalPlan The state of the plan before the change (can be null for CREATE).
     * @param updatedPlan The state of the plan after the change (can be null for DELETE).
     */
    public ProjectTaskChangeEvent(Object source, Long operatorId, String changeType, ProjectPlan originalPlan, ProjectPlan updatedPlan) {
        super(source, operatorId, changeType);
        this.originalPlan = originalPlan;
        this.updatedPlan = updatedPlan;
    }
}