package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.department.DepartmentTaskStatsResp;
import com.zkyzn.project_manager.stories.DepartmentPlanStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "api/department/plan", description = "科室计划相关")
@RequestMapping("api/department/plan")
public class DepartmentPlanController {

    @Resource
    private DepartmentPlanStory departmentPlanStory;

    @Operation(summary = "今日到期事项")
    @GetMapping(value = "/{departmentName}/stats")
    public Result<DepartmentTaskStatsResp> getDepartmentTaskStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentTaskStats(departmentName));
    }
}