package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectDocumentService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import com.zkyzn.project_manager.so.project_info.ProjectDocumentReq;
import com.zkyzn.project_manager.so.project_info.ProjectImportReq;
import com.zkyzn.project_manager.so.project_info.ProjectInfoResp;
import com.zkyzn.project_manager.utils.ExcelUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr-ti
 */
@Service
public class ProjectInfoStory {

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private ProjectDocumentService projectDocumentService;

    @Resource
    private ProjectPlanService projectPlanService;

    /**
     * 创建项目, 所有异常都会触发回滚
     *
     * @param req 项目请求参数
     * @return 项目编号
     */
    @Transactional(rollbackFor = Exception.class) //
    public String createProject(ProjectCreateReq req) {

        // 判断项目编号是否存在，如果存在则返回错误
        if (projectInfoService.existsByProjectNumber(req.getProjectNumber())) {
            return "项目编号已存在";
        }

        //  插入项目信息数据表
        boolean insert = projectInfoService.saveProject(req);

        List<ProjectDocumentReq> projectDocumentList = req.getProjectDocumentList();
        if (insert && !projectDocumentList.isEmpty()) {

            // 将项目文档插入项目文档表，创建项目时，projectDocumentList中projectId为空，需要将projectDocument的projectId字段设置为projectInfo的projectId
            ProjectInfo projectInfo = projectInfoService.getByProjectNumber(req.getProjectNumber());
            Long projectId = projectInfo.getProjectId();
            // 如果文档类型是计划书，需要解析其中的任务
            projectDocumentList.forEach(projectDocument -> {
                if (projectDocument.getProjectId() == null) {
                    projectDocument.setProjectId(projectId);
                    projectDocumentService.save(projectDocument);
                }
                if ("项目计划".equals(projectDocument.getDocumentType())) {

                    // 根据文件路径获取计划书，计划书是excel格式，需要解析计划书，获取任务列表
                    String filePath = projectDocument.getFilePath();
                    List<ProjectPlan> planList = ExcelUtil.parseProjectPlan(filePath, projectInfo.getProjectId());
                    if (!planList.isEmpty()) {
                        projectPlanService.saveBatch(planList);
                    }
                }
            });
        }

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }


    /**
     * 更新项目
     *
     * @param req 项目请求参数
     * @return 项目编号
     */
    public String updateProject(ProjectCreateReq req) {
        // 判断项目编号是否存在，如果不存在则返回错误
        if (!projectInfoService.existsByProjectNumber(req.getProjectNumber())) {
            return "项目编号已存在";
        }

        //  插入数据库 todo: 是否需要改成按照项目编号更新
        boolean insert = projectInfoService.updateById(req);

        // todo: 处理项目文档

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }

    /**
     * 根据项目编号获取项目信息
     *
     * @param projectNumber 项目编号
     * @return 项目信息
     */
    public ProjectInfoResp getProjectByProjectNumber(String projectNumber) {
        ProjectInfoResp resp = new ProjectInfoResp();

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo != null) {
            //复制父类属性
            BeanUtils.copyProperties(projectInfo, resp);
            resp.setProjectDocumentList(projectDocumentService.listByProjectId(resp.getProjectId()));
        }

        return resp;
    }


    /**
     * 分页查询项目信息
     *
     * @param current 当前页
     * @param size    分页大小
     * @return 项目信息
     */
    public Page<ProjectInfo> pageProjectInfo(int current, int size) {
        Page<ProjectInfo> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        return projectInfoService.pageProject(page.getCurrent(), page.getSize(), null);
    }

    /**
     * 删除项目
     *
     * @param projectNumber 项目编号
     * @return 是否删除成功
     */
    public Boolean deleteProjectByProjectNumber(String projectNumber) {
        return projectInfoService.removeByProjectNumber(projectNumber);
    }

    /**
     * 批量导入项目
     *
     * @param req 项目请求参数
     * @return 是否导入成功
     */
    public Boolean importProjectBatch(ProjectImportReq req) {
        List<ProjectInfo> projectInfoList = ExcelUtil.parseProjectInfoSheet(req.getImportExcelFilePath(), "");

        // todo: 输出错误文档

        //  批量插入项目信息数据表
        return projectInfoService.saveBatch(projectInfoList);
    }

}
