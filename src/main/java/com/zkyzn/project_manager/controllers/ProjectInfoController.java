package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.project_info.ProjectInfoReq;
import com.zkyzn.project_manager.so.project_info.ProjectImportReq;
import com.zkyzn.project_manager.so.project_info.ProjectInfoResp;
import com.zkyzn.project_manager.stories.ProjectInfoStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
            @RequestBody ProjectInfoReq req
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
            @RequestBody ProjectInfoReq req
    ) {
        // 如果projectNumber参数和req中的projectNumber不一致，则更新失败
        if (!projectNumber.equals(req.getProjectNumber())) {
            return ResUtil.fail("项目编号不一致！");
        }

        String projectId = projectInfoStory.updateProject(req);
        if (projectId == null) {
            return ResUtil.fail("更新失败！");
        }
        return ResUtil.ok(projectNumber);
    }

    @Operation(summary = "获取项目信息")
    @GetMapping("/{projectNumber}")
    public Result<ProjectInfoResp> getProjectByProjectNumber(
            @PathVariable("projectNumber") String projectNumber
    ) {
        ProjectInfoResp project = projectInfoStory.getProjectByProjectNumber(projectNumber);
        return ResUtil.ok(project);
    }

    @Operation(summary = "分页查询项目信息")
    @GetMapping("")
    public ResultList<ProjectInfo> pageProjectInfo(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "project_name", required = false) String projectName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "is_favorite", required = false) Boolean isFavorite,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "current_phase", required = false) String currentPhase,
            @RequestParam(value = "start_date_begin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateBegin,
            @RequestParam(value = "start_date_end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateEnd
    ) {
        ProjectInfo condition = new ProjectInfo();
        condition.setProjectName(projectName);
        condition.setStatus(status);
        condition.setIsFavorite(isFavorite);
        condition.setDepartment(department);
        condition.setCurrentPhase(currentPhase);

        Page<ProjectInfo> result = projectInfoStory.pageProjectInfo(
                page, size, condition, startDateBegin, startDateEnd
        );
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

    //添加一个收藏项目的方法
    @Operation(summary = "收藏项目")
    @PutMapping("/{projectNumber}/favorite")
    public Result<Boolean> favoriteProject(
            @PathVariable("projectNumber") String projectNumber,
            @RequestBody ProjectInfoReq req
    ) {
        boolean success = projectInfoStory.favoriteProject(projectNumber, req);
        return ResUtil.ok(success);
    }

    // 获取所属科室的接口
    @Operation(summary = "获取所有科室信息")
    @GetMapping("/department")
    public ResultList<String> getDepartment() {
        // 写死一个科室返回给前端
        List<String> departments = Arrays.asList("科室1");
        return ResUtil.list(departments);
    }
}

