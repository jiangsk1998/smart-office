package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.models.ProjectDocument;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.services.ProjectDocumentService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.so.project_info.ProjectCreateReq;
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

    /**
     * 创建项目, 所有异常都会触发回滚
     * @param req
     * @return
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
        if (insert) {

            ProjectInfo projectInfo = projectInfoService.getByProjectNumber(req.getProjectNumber());

            List<ProjectDocument> projectDocumentList = req.getProjectDocumentList();

            // 创建项目时，projectDocumentList中projectId为空，需要将projectId设置为projectInfo的projectId
            projectDocumentList.stream()
                    .filter(projectDocument -> projectDocument.getProjectId() == null)
                    .forEach(projectDocument -> projectDocument.setProjectId(projectInfo.getProjectId()));

            // 遍历projectDocumentList，获取filePath路径文件的

            insert = projectDocumentService.saveBatch(projectDocumentList, 100);
        }

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }


    /**
     * 更新项目
     * @param req
     * @return
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
     * @param projectNumber
     * @return
     */
    public ProjectInfo getProjectByProjectNumber(String projectNumber) {

        return projectInfoService.getByProjectNumber(projectNumber);
    }

    /**
     * 删除项目
     * @param projectId
     * @return
     */
    public Boolean deleteProject(String projectId) {
        return projectInfoService.removeByProjectNumber(projectId);
    }

}
