package com.zkyzn.project_manager.services;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.ProjectDecumentDao;
import com.zkyzn.project_manager.models.ProjectDocument;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr-ti
 * Date: 2025/6/8 21:04
 */
@Service
public class ProjectDocumentService extends MPJBaseServiceImpl<ProjectDecumentDao, ProjectDocument> {

    // listByProjectId
    public List<ProjectDocument> listByProjectId(Long projectId) {
        MPJLambdaQueryWrapper<ProjectDocument> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectDocument.class)
                .eq(ProjectDocument::getProjectId, projectId);
        return baseMapper.selectList(wrapper);
    }
}
