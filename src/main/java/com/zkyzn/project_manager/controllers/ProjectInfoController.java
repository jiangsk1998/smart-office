package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import com.zkyzn.project_manager.stories.ProjectInfoStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author Mr-ti
 */
@RestController
@Tag(name = "api/project/info", description = "项目信息管理")
@RequestMapping("api/project/info")
public class ProjectInfoController {

    @Resource
    private ProjectInfoStory projectInfoStory;

    @Operation(summary = "创建项目")
    @PostMapping()
    public Result<String> createProject(
            @RequestBody ProjectCreateReq req
    ) throws IOException {
        String projectId = projectInfoStory.createProject(req);
        if (projectId == null) {
            return ResUtil.fail("插入失败！");
        }
        return ResUtil.ok(projectId);
    }

    @Operation(summary = "更新项目")
    @PutMapping(value = "/{projectNumber}")
    public Result<String> updateProject(
            @PathVariable("projectNumber") String projectNumber,
            @RequestBody ProjectCreateReq req
    ) throws IOException {
        req.setProjectNumber(projectNumber);
        projectInfoStory.updateProject(req);
        if (projectNumber == null) {
            return ResUtil.fail("更新失败！");
        }
        return ResUtil.ok(projectNumber);
    }

    @Operation(summary = "获取项目详情")
    @GetMapping("/{projectNumber}")
    public Result<ProjectInfo> getProjectById(
            @PathVariable("projectNumber") String projectNumber
    ) throws IOException {
        ProjectInfo project = projectInfoStory.getProjectByProjectNumber(projectNumber);
        return ResUtil.ok(project);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{projectNumber}")
    public Result<Boolean> deleteProject(
            @PathVariable("projectNumber") String projectNumber
    ) throws IOException {
        boolean success = projectInfoStory.deleteProject(projectNumber);
        return ResUtil.ok(success);
    }
}

