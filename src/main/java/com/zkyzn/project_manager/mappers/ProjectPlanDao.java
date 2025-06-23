package com.zkyzn.project_manager.mappers;


import com.github.yulichang.base.MPJBaseMapper;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.personnel.PersonnelTodoTaskResp;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/8 23:03
 */
@Repository
public interface ProjectPlanDao extends MPJBaseMapper<ProjectPlan> {


    /**
     * 根据责任人和时间范围查询待办事项（XML实现）
     *
     * @param personName 责任人姓名
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param statuses   任务状态列表 ('未开始', '中止')
     * @return 包含项目信息的待办任务列表
     */
    List<PersonnelTodoTaskResp> findTodoTasksForPersonXML(
            @Param("personName") String personName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<String> statuses
    );

    Integer countPlansByDateRangeAndStatusForPerson(
            @Param("personName") String personName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);

    Integer countPlansByDateRangeAndUncompletedStatusForPerson(
            @Param("personName") String personName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("completedStatus") String completedStatus,
            @Param("stopStatus") String stopStatus);

    Integer countPlansByDateRangeForPerson(
            @Param("personName") String personName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
