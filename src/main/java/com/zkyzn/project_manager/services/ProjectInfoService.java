package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.ProjectInfoDao;
import com.zkyzn.project_manager.models.ProjectInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

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
     * 通过项目ID查询（主键查询）
     * @param projectId 项目Id（VARCHAR类型）
     */
    public ProjectInfo getByProjectId(String projectId) {
        return baseMapper.selectById(projectId);
    }

    /**
     * 根据项目工号查询项目
     * @param projectNumber 项目工号
     * @return 匹配的项目信息实体或null
     */
    public ProjectInfo getByProjectNumber(String projectNumber) {
        if (!StringUtils.hasText(projectNumber)) {
            return null;
        }

        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectInfo.class)
                .eq(ProjectInfo::getProjectNumber, projectNumber)
                .last("LIMIT 1");  // 确保只返回一条记录

        return baseMapper.selectOne(wrapper);
    }

    /**
     * 检查项目工号是否存在
     * @param projectNumber 项目工号
     * @return 存在返回 true
     */
    public boolean existsByProjectNumber(String projectNumber) {
        if (!StringUtils.hasText(projectNumber)) {
            return false;
        }

        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectInfo::getProjectId)
                .eq(ProjectInfo::getProjectNumber, projectNumber);

        return baseMapper.selectCount(wrapper) > 0;
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

    /**
     * 通过项目工号删除项目
     * @param projectNumber 项目工号
     * @return 是否删除成功
     */
    public boolean removeByProjectNumber(String projectNumber) {
        ProjectInfo project = getByProjectNumber(projectNumber);

        if (project == null) {
            return false;
        }

        // 执行删除
        return baseMapper.deleteById(project.getProjectId()) > 0;
    }
}

