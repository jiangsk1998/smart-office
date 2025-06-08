package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (projectInfoService.existsByProjectNumber(req.getProjectNumber())) {
            return "项目编号已存在";
        }

        // todo: 处理项目文档

        //  插入数据库
        boolean insert =projectInfoService.save(req);

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }



    public String updateProject(ProjectCreateReq req) throws IOException {
        // 判断项目编号是否存在，如果不存在则返回错误
        if (!projectInfoService.existsByProjectNumber(req.getProjectNumber())) {
            return "项目编号已存在";
        }

        //  插入数据库 todo: 是否需要改成按照项目编号更新
        boolean insert =projectInfoService.updateById(req);

        // todo: 处理项目文档

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }

    public ProjectInfo getProjectByProjectNumber(String projectNumber) throws IOException {

        ProjectInfo byProjectId = projectInfoService.getByProjectNumber(projectNumber);
        if (byProjectId == null) return null;
        return byProjectId;
    }

    public Boolean deleteProject(String projectId) throws IOException {
        return projectInfoService.removeByProjectNumber(projectId);
    }

}
