
package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.Department;
import com.zkyzn.project_manager.services.DepartmentService;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "api/department", description = "科室管理")
@RequestMapping("/api/department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @Operation(summary = "获取所有科室列表")
    @GetMapping("/list")
    public ResultList<Department> getAllDepartments() {
        List<Department> departments = departmentService.list(); // 调用 DepartmentService的list方法获取所有部门
        return ResUtil.list(departments);
    }

    @Operation(summary = "获取科室树形结构")
    @GetMapping("/tree")
    public ResultList<Department> getDepartmentTree() {
        List<Department> departmentTree = departmentService.getDepartmentTree(); // 调用DepartmentService的getDepartmentTree方法
        return ResUtil.list(departmentTree);
    }

    // 你可以根据需要添加其他部门相关的增删改查接口
    // 例如：
    // @Operation(summary = "根据ID获取科室详情")
    // @GetMapping("/{id}")
    // public Result<Department> getDepartmentById(@PathVariable Long id) {
    //     Department department = departmentService.getDepartmentById(id);
    //     return department != null ? ResUtil.ok(department) : ResUtil.fail("科室不存在");
    // }

    // @Operation(summary = "创建科室")
    // @PostMapping
    // public Result<Department> createDepartment(@RequestBody Department department) {
    //     departmentService.save(department);
    //     return ResUtil.ok(department);
    // }
}