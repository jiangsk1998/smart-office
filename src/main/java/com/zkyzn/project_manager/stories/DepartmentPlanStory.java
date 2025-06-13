package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.department.DepartmentTaskStatsResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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

    public DepartmentTaskStatsResp getDepartmentTaskStats(String departmentName) {
        DepartmentTaskStatsResp response = new DepartmentTaskStatsResp();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 1. 今日到期数
        long todayDueCount = projectPlanService.countTasksDueOnDate(departmentName, today);
        response.setTodayDueCount(todayDueCount);

        // 2. 今日到期与昨日到期变化百分比
        long yesterdayDueCount = projectPlanService.countTasksDueOnDate(departmentName, yesterday);
        if (yesterdayDueCount > 0) {
            BigDecimal change = BigDecimal.valueOf(todayDueCount - yesterdayDueCount)
                    .divide(BigDecimal.valueOf(yesterdayDueCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setChangePercentage(String.format("%+.2f%%", change));
        } else if (todayDueCount > 0) {
            response.setChangePercentage("+100.00%");
        } else {
            response.setChangePercentage("0.00%");
        }

        // 3. 近10日的每日到期数
        List<DepartmentTaskStatsResp.DailyCount> dailyCounts = new ArrayList<>();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = projectPlanService.countTasksDueOnDate(departmentName, date);
            dailyCounts.add(new DepartmentTaskStatsResp.DailyCount(date, count));
        }
        response.setLast10DaysDueCounts(dailyCounts);

        return response;
    }
}