package com.zkyzn.project_manager.services;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.ProjectInfoDao;
import com.zkyzn.project_manager.models.ProjectInfo;
import org.springframework.stereotype.Service;

/**
 * Copyright(C) 2024 HFHX.All right reserved.
 * ClassName: ProjectInfoService
 * Description: TODO
 * Version: 1.0
 * Author: Mr-ti
 * Date: 2025/6/7 16:42
 */
@Service
public class ProjectInfoService extends MPJBaseServiceImpl<ProjectInfoDao, ProjectInfo> {

    /**
     * 通过项目工号查询（主键查询）
     * @param projectId 项目工号（VARCHAR类型）
     */
    public ProjectInfo getByProjectId(String projectId) {
        return baseMapper.selectById(projectId);
    }

    /**
     * 通过项目工号判断项目是否存在
     */
    public boolean existsByProjectId(String projectId) {
        return baseMapper.existsById(projectId);
    }

    /**
     * 通过项目名称模糊查询
     * @param projectName 项目名称
     */
    public ProjectInfo getByProjectName(String projectName) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.like(ProjectInfo::getProjectName, projectName);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 通过科室ID查询
     * @param department 科室
     */
    public ProjectInfo getByDepartment(String department) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.eq(ProjectInfo::getDepartment, department);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 保存项目
     */
    public boolean saveProject(ProjectInfo projectInfo) {
        return save(projectInfo);
    }
}

