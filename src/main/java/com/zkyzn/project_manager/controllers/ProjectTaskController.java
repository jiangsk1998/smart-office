package com.zkyzn.project_manager.controllers;

import cn.hutool.core.bean.BeanUtil;
import com.zkyzn.project_manager.annotation.OperLog;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.task.ProjectTaskReq;
import com.zkyzn.project_manager.stories.ProjectTaskStory;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
import com.zkyzn.project_manager.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jiangsk
 */
@RestController
@RequestMapping("/api/project/task")
@Tag(name = "/api/project/task",description = "项目任务管理")
public class ProjectTaskController {

    @Resource
    private ProjectTaskStory projectTaskStory;

    @PostMapping
    @Operation(summary = "新增项目任务")
    @OperLog(type = "CREATE", desc = "新增项目任务", targetType = ProjectPlan.class)
    public Result<Boolean> postCreatePlan(@RequestBody @Valid ProjectTaskReq task) {
        ProjectPlan projectPlan = new ProjectPlan();
        BeanUtil.copyProperties(task, projectPlan);
        projectPlan.setTaskStatus(task.getTaskStatus().getDisplayName());
        return ResUtil.ok(projectTaskStory.createPlan(projectPlan,SecurityUtil.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑项目任务")
    @OperLog(type = "UPDATE", desc = "编辑项目任务", targetType = ProjectPlan.class, idPosition = 0, recordOriginal = true)
    public Result<Boolean> updatePlan(@PathVariable Long id, @RequestBody @Valid ProjectTaskReq task) {
        ProjectPlan projectPlan = new ProjectPlan();
        BeanUtil.copyProperties(task, projectPlan);
        projectPlan.setProjectPlanId(id);
        return ResUtil.ok(projectTaskStory.updatePlanById(projectPlan, SecurityUtil.getCurrentUserId()));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "变更项目任务状态")
    @OperLog(type = "UPDATE", desc = "变更项目任务状态", targetType = ProjectPlan.class, idPosition = 0, recordOriginal = true)
    public Result<Boolean> putChangePlanStatus(@PathVariable Long id, @RequestParam TaskStatusEnum status) {
        return ResUtil.ok(projectTaskStory.changePlanStatus(id, status.getDisplayName(), SecurityUtil.getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目任务")
    @OperLog(type = "DELETE", desc = "删除项目任务", targetType = ProjectPlan.class, idPosition = 0, recordOriginal = true)
    public Result<Boolean> deletePlanById(@PathVariable Long id) {
        return ResUtil.ok(projectTaskStory.deletePlanById(id,SecurityUtil.getCurrentUserId()));
    }
}