package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.stories.ProjectTaskStory;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 *   Todo 获取当前操作用户
 * @author Jiangsk
 */
@RestController
@RequestMapping("/api/project/task")
@Tag(name = "项目任务管理")
public class ProjectTaskController {

    @Resource
    private ProjectTaskStory projectTaskStory;

    @PostMapping
    @Operation(summary = "新增项目任务")
    public Result<Boolean> postCreatePlan(@RequestBody ProjectPlan projectPlan) {
        return ResUtil.ok(projectTaskStory.createPlan(projectPlan,1L));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑项目任务")
    public Result<Boolean> updatePlan(@PathVariable Long id, @RequestBody ProjectPlan projectPlan) {
        projectPlan.setProjectPlanId(id);
        return ResUtil.ok(projectTaskStory.updatePlanById(projectPlan, 1L));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "变更项目任务状态")
    public Result<Boolean> putChangePlanStatus(@PathVariable Long id, @RequestParam String status) {
        return ResUtil.ok(projectTaskStory.changePlanStatus(id, status, 1L));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目任务")
    public Result<Boolean> deletePlanById(@PathVariable Long id) {
        return ResUtil.ok(projectTaskStory.deletePlanById(id,1L));
    }
}
