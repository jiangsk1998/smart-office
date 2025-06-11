package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.stories.ProjectPhaseStory;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
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
    public Result<Boolean> postCreatePhase(@RequestBody ProjectPhase projectPhase) {

        // Todo 获取当前操作用户
        return ResUtil.ok(projectPhaseStory.createPhase(projectPhase,1L));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑项目阶段")
    public Result<Boolean> updatePhase(@PathVariable Long id, @RequestBody ProjectPhase projectPhase) {
        projectPhase.setPhaseId(id);
        // Todo 获取当前操作用户
        return ResUtil.ok(projectPhaseStory.updatePhaseById(projectPhase ,1L));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "变更项目阶段状态")
    public Result<Boolean> putChangePhaseStatus(@PathVariable Long id, @RequestParam String status) {
        // Todo 获取当前操作用户
        return ResUtil.ok(projectPhaseStory.changePhaseStatus(id, status,1L));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目阶段")
    public Result<Boolean> deletePhaseById(@PathVariable Long id) {
        // Todo 获取当前操作用户
        return ResUtil.ok(projectPhaseStory.deletePhaseById(id,1L));
    }
}
