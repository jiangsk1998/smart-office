package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import com.zkyzn.project_manager.stories.ProjectInfoStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(value = "/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> updateProject(
            @PathVariable("projectId") String projectId,
            @RequestBody ProjectCreateReq req
    ) throws IOException {
        req.setProjectId(projectId);
        projectInfoStory.updateProject(req);
        if (projectId == null) {
            return ResUtil.fail("更新失败！");
        }
        return ResUtil.ok(projectId);
    }

    @Operation(summary = "获取项目详情")
    @GetMapping("/{projectId}")
    public Result<ProjectInfo> getProjectById(
            @PathVariable("projectId") String projectId
    ) throws IOException {
        ProjectInfo project = projectInfoStory.getProjectById(projectId);
        return ResUtil.ok(project);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{projectId}")
    public Result<Boolean> deleteProject(
            @PathVariable("projectId") String projectId
    ) throws IOException {
        boolean success = projectInfoStory.deleteProject(projectId);
        return ResUtil.ok(success);
    }
}

