package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectPlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * 封装与科室计划相关的业务场景
 */
@Service
public class DepartmentPlanStory {

    @Resource
    private ProjectPlanService projectPlanService;

    /**
     * 根据科室名称，分页查询该科室承担的所有项目任务。
     *
     * @param departmentName 科室的准确名称
     * @param pageNum        当前页码
     * @param pageSize       每页显示条数
     * @return 分页后的项目任务列表 (Page<ProjectPlan>)
     */
    public Page<ProjectPlan> getTasksForDepartment(String departmentName, long pageNum, long pageSize) {
        // 创建分页对象
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);

        // 如果部门名称为空，则返回空的分页结果
        if (!StringUtils.hasText(departmentName)) {
            return page;
        }

        // 使用 ProjectPlanService 进行条件查询
        // 查询条件为：ProjectPlan实体中的department字段等于传入的departmentName
        return projectPlanService.lambdaQuery()
                .eq(ProjectPlan::getDepartment, departmentName)
                .page(page);

    }
}