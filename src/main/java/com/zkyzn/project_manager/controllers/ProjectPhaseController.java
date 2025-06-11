package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.annotation.OperLog;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.stories.ProjectPhaseStory;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
import com.zkyzn.project_manager.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project/phase")
@Tag(name = "项目阶段管理")
public class ProjectPhaseController {

    @Resource
    private ProjectPhaseStory projectPhaseStory;

    @PostMapping
    @Operation(summary = "新增项目阶段")
    @OperLog(type = "CREATE", desc = "新增项目阶段", targetType = ProjectPhase.class)
    public Result<Boolean> postCreatePhase(@RequestBody ProjectPhase projectPhase) {
        return ResUtil.ok(projectPhaseStory.createPhase(projectPhase, SecurityUtil.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑项目阶段")
    @OperLog(type = "UPDATE", desc = "编辑项目阶段", targetType = ProjectPhase.class, idPosition = 0, recordOriginal = true)
    public Result<Boolean> updatePhase(@PathVariable Long id, @RequestBody ProjectPhase projectPhase) {
        projectPhase.setPhaseId(id);
        return ResUtil.ok(projectPhaseStory.updatePhaseById(projectPhase ,SecurityUtil.getCurrentUserId()));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "变更项目阶段状态")
    @OperLog(type = "UPDATE", desc = "变更项目阶段状态", targetType = ProjectPhase.class, idPosition = 0, recordOriginal = true)
    public Result<Boolean> putChangePhaseStatus(@PathVariable Long id, @RequestParam String status) {
        return ResUtil.ok(projectPhaseStory.changePhaseStatusById(id, status,SecurityUtil.getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目阶段")
    @OperLog(type = "DELETE", desc = "删除项目阶段", targetType = ProjectPhase.class, idPosition = 0, recordOriginal = true)
    public Result<Boolean> deletePhaseById(@PathVariable Long id) {
        return ResUtil.ok(projectPhaseStory.deletePhaseById(id,SecurityUtil.getCurrentUserId()));
    }
}