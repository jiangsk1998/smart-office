package com.zkyzn.project_manager.so.project.dashboard;

import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import lombok.Data;

import java.util.List;

/**
 * 阶段详情类，继承自阶段类，添加任务列表字段
 * @author: Mr-ti
 * Date: 2025/6/13 20:15
 */
@Data
public class PhaseDetail extends ProjectPhase {

    // 阶段下的任务列表
    private List<ProjectPlan> projectPlanList;

}
