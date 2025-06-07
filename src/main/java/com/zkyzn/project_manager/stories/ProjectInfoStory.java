package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;

/**
 * Copyright(C) 2024 HFHX.All right reserved.
 * ClassName: ProjectInfoStory
 * Description: TODO
 * Version: 1.0
 * Author: Mr-ti
 * Date: 2025/6/6 16:44
 */
@Service
public class ProjectInfoStory {

    public ProjectInfoStory(ProjectInfoService projectInfoService) {
        this.projectInfoService = projectInfoService;
    }

    private final ProjectInfoService projectInfoService;

    public String createProject(ProjectCreateReq req) throws IOException {

        // 判断项目编号是否存在，如果存在则返回错误
        if (projectInfoService.existsByProjectId(req.getProjectId())) {
            return "项目编号已存在";
        }

        //  创建项目实体类
        ProjectInfo projectInfo = getProjectInfo(req);

        //  插入数据库
        boolean insert =projectInfoService.save(projectInfo);

        //  如果插入成功则返回项目编号
        return insert ? projectInfo.getProjectId() : null;
    }



    public String updateProject(ProjectCreateReq req) throws IOException {
        // 判断项目编号是否存在，如果不存在则返回错误
        if (!projectInfoService.existsByProjectId(req.getProjectId())) {
            return "项目编号已存在";
        }

        //  创建项目实体类
        ProjectInfo projectInfo = getProjectInfo(req);

        //  插入数据库
        boolean insert =projectInfoService.updateById(projectInfo);

        //  如果插入成功则返回项目编号
        return insert ? projectInfo.getProjectId() : null;
    }

    public ProjectInfo getProjectById(String projectId) throws IOException {
        ProjectInfo byProjectId = projectInfoService.getByProjectId(projectId);
        if (byProjectId == null) return null;
        return byProjectId;
    }

    public Boolean deleteProject(String projectId) throws IOException {
        return projectInfoService.removeById(projectId);
    }

    /**
     * 将项目创建请求参数转换为项目实体对象
     *
     * @param req 包含项目创建所需参数的请求对象
     * @return 返回配置好的ProjectInfo实体对象实例
     */
    private static ProjectInfo getProjectInfo(ProjectCreateReq req) {
        if (req == null) return null;

        ProjectInfo projectInfo = new ProjectInfo();

        // 基础信息
        projectInfo.setProjectId(req.getProjectId());
        projectInfo.setProjectName(req.getProjectName());
        projectInfo.setDepartment(req.getDepartment());
        projectInfo.setStartDate(req.getStartDate().atStartOfDay(ZoneId.systemDefault()).toLocalDate());
        projectInfo.setEndDate(req.getEndDate().atStartOfDay(ZoneId.systemDefault()).toLocalDate());
        projectInfo.setResponsibleLeaderId(req.getResponsibleLeaderID());
        projectInfo.setTechnicalLeaderId(req.getTechnicalLeaderId());
        projectInfo.setPlanSupervisorId(req.getPlanSupervisorId());
        projectInfo.setCreatorId(req.getCreatorId());

        return projectInfo;
    }

}
