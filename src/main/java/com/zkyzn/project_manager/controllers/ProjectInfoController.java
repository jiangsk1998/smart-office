package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import com.zkyzn.project_manager.so.project_info.ProjectImportReq;
import com.zkyzn.project_manager.stories.ProjectInfoStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    ) {
        String projectId = projectInfoStory.createProject(req);
        if (projectId == null) {
            return ResUtil.fail("插入失败！");
        }
        return ResUtil.ok(projectId);
    }

    @Operation(summary = "批量导入项目")
    @PostMapping(value = "/batch")
    public Result<Boolean> importProjectBatch(
            @RequestBody ProjectImportReq req
    ) {
        boolean success = projectInfoStory.importProjectBatch(req);
        if (!success) {
            return ResUtil.fail("导入失败！");
        }
        return ResUtil.ok(true);
    }

    @Operation(summary = "更新项目")
    @PutMapping(value = "/{projectNumber}")
    public Result<String> updateProject(
            @PathVariable("projectNumber") String projectNumber,
            @RequestBody ProjectCreateReq req
    ) {
        req.setProjectNumber(projectNumber);
        String projectId = projectInfoStory.updateProject(req);
        if (projectId == null) {
            return ResUtil.fail("更新失败！");
        }
        return ResUtil.ok(projectNumber);
    }

    @Operation(summary = "获取项目信息")
    @GetMapping("/{projectNumber}")
    public Result<ProjectInfo> getProjectByProjectNumber(
            @PathVariable("projectNumber") String projectNumber
    ) {
        ProjectInfo project = projectInfoStory.getProjectByProjectNumber(projectNumber);
        return ResUtil.ok(project);
    }

    @Operation(summary = "分页查询项目信息")
    @GetMapping("")
    public ResultList<ProjectInfo> pageProjectInfo(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        Page<ProjectInfo> result = projectInfoStory.pageProjectInfo(page, size);
        return ResUtil.list(result);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{projectNumber}")
    public Result<Boolean> deleteProjectByProjectNumber(
            @PathVariable("projectNumber") String projectNumber
    ) {
        boolean success = projectInfoStory.deleteProjectByProjectNumber(projectNumber);
        return ResUtil.ok(success);
    }
}

