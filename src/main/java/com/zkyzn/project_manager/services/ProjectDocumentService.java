package com.zkyzn.project_manager.services;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zkyzn.project_manager.mappers.ProjectDecumentDao;
import com.zkyzn.project_manager.models.ProjectDocument;
import com.zkyzn.project_manager.models.ProjectInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Mr-ti
 * Date: 2025/6/8 21:04
 */
@Service
public class ProjectDocumentService extends MPJBaseServiceImpl<ProjectDecumentDao, ProjectDocument> {

    /**
     * 根据项目id查询文档列表
     * @param projectId
     * @return
     */
    public List<ProjectDocument> listByProjectId(Long projectId) {
        MPJLambdaQueryWrapper<ProjectDocument> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectDocument.class)
                .eq(ProjectDocument::getProjectId, projectId);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据项目类型删除文档
     * @param documentType
     * @return
     */
    public boolean removeByProjectType(String documentType) {
        ProjectDocument document = getByProjectType(documentType);

        if (document == null) {
            return false;
        }

        // 执行删除
        return baseMapper.deleteById(document.getProjectDocumentId()) > 0;
    }

    /**
     * 根据项目类型获取项目文档
     * @param documentType
     * @return
     */
    public ProjectDocument getByProjectType (String documentType) {
        if (!StringUtils.hasText(documentType)) {
            return null;
        }

        MPJLambdaQueryWrapper<ProjectDocument> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectDocument.class)
                .eq(ProjectDocument::getDocumentType, documentType)
                .last("LIMIT 1");

        return baseMapper.selectOne(wrapper);
    }

    public List<ProjectDocument> getByDepartment(String department) {
        MPJLambdaWrapper<ProjectDocument> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(ProjectDocument.class)
                .innerJoin(ProjectInfo.class, on -> on.eq(ProjectDocument::getProjectId, ProjectInfo::getProjectId))
                .eq(ProjectInfo::getDepartment, department);
        return baseMapper.selectList(wrapper);
    }
}
