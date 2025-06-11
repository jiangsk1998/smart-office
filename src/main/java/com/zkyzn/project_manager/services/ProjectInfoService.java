package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.ProjectInfoDao;
import com.zkyzn.project_manager.models.ProjectInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Mr-ti
 */
@Service
public class ProjectInfoService extends MPJBaseServiceImpl<ProjectInfoDao, ProjectInfo> {

    /**
     * 通过项目ID查询（主键查询）
     *
     * @param projectId 项目Id（VARCHAR类型）
     */
    public ProjectInfo getByProjectId(String projectId) {
        return baseMapper.selectById(projectId);
    }

    public ProjectInfo getByProjectId(Long projectId) {
        return baseMapper.selectById(projectId);
    }


    /**
     * 根据项目工号查询项目
     *
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
                .last("LIMIT 1");

        return baseMapper.selectOne(wrapper);
    }

    /**
     * 检查项目工号是否存在
     *
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
     *
     * @param projectName 项目名称
     */
    public ProjectInfo getByProjectName(String projectName) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.like(ProjectInfo::getProjectName, projectName);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 通过科室ID查询
     *
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
     *
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

    /**
     * 分页查询项目信息
     *
     * @param pageNum     当前页码
     * @param pageSize    每页大小
     * @param projectInfo 查询条件（可选）
     * @return 分页结果
     */
    public Page<ProjectInfo> pageProject(Long pageNum, Long pageSize, ProjectInfo projectInfo) {
        // 创建分页对象
        Page<ProjectInfo> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();

        // 明确指定 SELECT 字段（至少选择一个字段）
        wrapper.selectAll(ProjectInfo.class);

        // 添加动态查询条件
        if (projectInfo != null) {
            if (StringUtils.hasText(projectInfo.getProjectNumber())) {
                wrapper.like(ProjectInfo::getProjectNumber, projectInfo.getProjectNumber());
            }
            if (StringUtils.hasText(projectInfo.getProjectName())) {
                wrapper.like(ProjectInfo::getProjectName, projectInfo.getProjectName());
            }
            if (StringUtils.hasText(projectInfo.getDepartment())) {
                wrapper.eq(ProjectInfo::getDepartment, projectInfo.getDepartment());
            }
            if (StringUtils.hasText(projectInfo.getStatus())) {
                wrapper.eq(ProjectInfo::getStatus, projectInfo.getStatus());
            }
            if (StringUtils.hasText(projectInfo.getCurrentPhase())) {
                wrapper.eq(ProjectInfo::getCurrentPhase, projectInfo.getCurrentPhase());
            }
            if (projectInfo.getStartDate() != null) {
                wrapper.ge(ProjectInfo::getStartDate, projectInfo.getStartDate());
            }
            if (projectInfo.getEndDate() != null) {
                wrapper.le(ProjectInfo::getEndDate, projectInfo.getEndDate());
            }
        }

        // 默认按创建时间倒序排序
        wrapper.orderByDesc(ProjectInfo::getCreateTime);

        // 执行分页查询
        return baseMapper.selectPage(page, wrapper);
    }
}

