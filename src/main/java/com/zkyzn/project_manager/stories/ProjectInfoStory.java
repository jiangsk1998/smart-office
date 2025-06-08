package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.models.ProjectDocument;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectDocumentService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
import com.zkyzn.project_manager.utils.ExcelUtil;
import jakarta.annotation.Resource;
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
        boolean insert = projectInfoService.save(req);

        // 将项目文档插入项目文档表
        List<ProjectDocument> projectDocumentList = req.getProjectDocumentList();
        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(req.getProjectNumber());
        if (insert && !projectDocumentList.isEmpty()) {

            // 创建项目时，projectDocumentList中projectId为空，需要将projectId设置为projectInfo的projectId
            projectDocumentList.stream()
                    .filter(projectDocument -> projectDocument.getProjectId() == null)
                    .forEach(projectDocument -> projectDocument.setProjectId(projectInfo.getProjectId()));

            // 遍历projectDocumentList，获取filePath路径文件的

            insert = projectDocumentService.saveBatch(projectDocumentList, 100);
        }

        // 如果文档类型是计划书，需要解析其中的任务
        if (!projectDocumentList.isEmpty()) {
            projectDocumentList.stream()
                    .filter(projectDocument -> "项目计划".equals(projectDocument.getDocumentType()))
                    .forEach(projectDocument -> {
                        // 解析计划书，获取任务列表
                        // 根据文件路径获取计划书，计划书是excel格式，需要解析
                        String filePath = projectDocument.getFilePath();
                        List<ProjectPlan> planList = ExcelUtil.parseProjectPlan(filePath);

                        planList.stream()
                                .filter(planItems -> planItems.getProjectId() == null)
                                .forEach(planItems -> planItems.setProjectId(projectInfo.getProjectId()));

                        if (!planList.isEmpty()) {
                            projectPlanService.saveBatch(planList);
                        }
                    });
        }

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }


    /**
     * 更新项目
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
     * @param projectNumber 项目编号
     * @return 项目信息
     */
    public ProjectInfo getProjectByProjectNumber(String projectNumber) {

        return projectInfoService.getByProjectNumber(projectNumber);
    }

    /**
     * 删除项目
     * @param projectNumber 项目编号
     * @return 是否删除成功
     */
    public Boolean deleteProject(String projectNumber) {
        return projectInfoService.removeByProjectNumber(projectNumber);
    }

}
