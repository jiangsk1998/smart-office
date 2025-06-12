package com.zkyzn.project_manager.events;

import com.zkyzn.project_manager.models.ProjectPhase;
import lombok.Getter;

@Getter
public class ProjectPhaseChangeEvent extends ProjectChangeEvent {

    private final ProjectPhase originalPhase; // 存储变更前的数据
    private final ProjectPhase updatedPhase;  // 存储变更后的数据

    /**
     * @param source The object on which the event initially occurred (can be 'this').
     * @param operatorId The ID of the user who triggered the event.
     * @param changeType The type of change (e.g., "CREATE", "UPDATE", "DELETE").
     * @param originalPhase The state of the phase before the change (can be null for CREATE).
     * @param updatedPhase The state of the phase after the change (can be null for DELETE).
     */
    public ProjectPhaseChangeEvent(Object source, Long operatorId, String changeType, ProjectPhase originalPhase, ProjectPhase updatedPhase) {
        super(source, operatorId, changeType);
        this.originalPhase = originalPhase;
        this.updatedPhase = updatedPhase;
    }
}