package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.enums.ProjectStatusEnum;
import com.zkyzn.project_manager.mappers.ProjectInfoDao;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Mr-ti
 */
@Service
public class ProjectInfoService extends MPJBaseServiceImpl<ProjectInfoDao, ProjectInfo> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectInfoService.class);

    /**
     * 通过项目ID查询（主键查询）
     *
     * @param projectId 项目Id（VARCHAR类型）
     */
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
        if (org.apache.commons.lang3.StringUtils.isBlank(projectNumber)) {
            return new ProjectInfo();
        }

        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectInfo.class)
                .eq(ProjectInfo::getProjectNumber, projectNumber)
                .last("LIMIT 1");

        ProjectInfo projectInfo = baseMapper.selectOne(wrapper); // 获取查询结果

        // 添加日志，打印原始ProjectInfo对象及其plan_supervisors字段
        if (projectInfo != null) {
            logger.info("Fetched ProjectInfo for projectNumber {}: {}", projectNumber, JsonUtil.toJson(projectInfo));
            if (projectInfo.getPlanSupervisors() == null) {
                logger.warn("plan_supervisors is null after fetching for projectNumber: {}", projectNumber);
            } else {
                logger.info("plan_supervisors value: {}", JsonUtil.toJson(projectInfo.getPlanSupervisors()));
            }
        } else {
            logger.warn("No ProjectInfo found for projectNumber: {}", projectNumber);
        }

        return projectInfo;
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
    public Page<ProjectInfo> pageProject(
            Long pageNum,
            Long pageSize,
            ProjectInfo projectInfo,
            LocalDate startDateBegin,
            LocalDate startDateEnd
    ) {
        Page<ProjectInfo> page = new Page<>(pageNum, pageSize);
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectInfo.class);

        // 添加基础条件
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
            if (projectInfo.getIsFavorite() != null) {
                wrapper.eq(ProjectInfo::getIsFavorite, projectInfo.getIsFavorite());
            }
        }

        // 添加日期范围条件
        if (startDateBegin != null) {
            wrapper.ge(ProjectInfo::getStartDate, startDateBegin);
        }
        if (startDateEnd != null) {
            wrapper.le(ProjectInfo::getStartDate, startDateEnd);
        }

        wrapper.orderByDesc(ProjectInfo::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 获取指定日期到期但未完成的项目列表。
     *
     * @param dueDate     截止日期
     * @param completedStatus 已完成状态的名称
     * @return 延期项目列表
     */
    public List<ProjectInfo> getDelayedProjects(LocalDate dueDate, String completedStatus) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectInfo.class) // 选择所有字段
                .eq(ProjectInfo::getEndDate, dueDate) // 结束日期是指定日期
                .ne(ProjectInfo::getStatus, completedStatus); // 状态不是“已完成”
        return baseMapper.selectList(wrapper);
    }

    public List<ProjectInfo> findByStatus(String status) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectInfo.class)
                .eq(ProjectInfo::getStatus, status);
        return baseMapper.selectList(wrapper);
    }

    public long countOverdueProjectsByDate(LocalDate date) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectInfo::getProjectId)
                .eq(ProjectInfo::getStatus, ProjectStatusEnum.OVERDUE.name())
                .le(ProjectInfo::getEndDate, date);
        return baseMapper.selectCount(wrapper);
    }

    public long countActiveProjectsByDate(LocalDate date) {
        MPJLambdaQueryWrapper<ProjectInfo> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectInfo::getProjectId)
                .eq(ProjectInfo::getStatus, ProjectStatusEnum.IN_PROGRESS.name())
                .le(ProjectInfo::getStartDate, date)
                .ge(ProjectInfo::getEndDate, date);
        return baseMapper.selectCount(wrapper);
    }
}