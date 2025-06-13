package com.zkyzn.project_manager.so.project.info;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.so.project.dashboard.PhaseDetail;
import com.zkyzn.project_manager.so.project.dashboard.PhaseProgress;
import lombok.Data;

import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/12 17:12
 */
@Data
public class ProjectDetailResp {
    // 项目基本信息
    private ProjectInfo projectInfo;

    // 项目的阶段信息列表
    private List<PhaseDetail> phaseDetails;

    // 阶段进度列表
    private List<PhaseProgress> phaseProgressList;
}