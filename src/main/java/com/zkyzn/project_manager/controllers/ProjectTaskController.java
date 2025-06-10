package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.stories.ProjectPlanStory;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project/task")
@Tag(name = "项目任务管理")
public class ProjectTaskController {

    @Resource
    private ProjectPlanStory projectPlanStory;

    @PostMapping
    @Operation(summary = "新增项目任务")
    public Result<Boolean> postCreatePlan(@RequestBody ProjectPlan projectPlan) {
        return ResUtil.ok(projectPlanStory.createPlan(projectPlan));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑项目任务")
    public Result<Boolean> updatePlan(@PathVariable Long id, @RequestBody ProjectPlan projectPlan) {
        projectPlan.setProjectPlanId(id);
        return ResUtil.ok(projectPlanStory.updatePlanById(projectPlan));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "变更项目任务状态")
    public Result<Boolean> putChangePlanStatus(@PathVariable Long id, @RequestParam String status) {
        return ResUtil.ok(projectPlanStory.changePlanStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目任务")
    public Result<Boolean> deletePlanById(@PathVariable Long id) {
        return ResUtil.ok(projectPlanStory.deletePlanById(id));
    }
}
