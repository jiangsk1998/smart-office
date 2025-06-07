package com.zkyzn.project_manager.mappers;


import com.github.yulichang.base.MPJBaseMapper;
import com.zkyzn.project_manager.models.ProjectInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Copyright(C) 2024 HFHX.All right reserved.
 * ClassName: ProjectInfoDao
 * Description: TODO
 * Version: 1.0
 * Author: Mr-ti
 * Date: 2025/6/7 16:43
 */
@Repository
public interface ProjectInfoDao extends MPJBaseMapper<ProjectInfo> {

    @Select("SELECT COUNT(*) FROM tab_project_info WHERE project_id = #{projectId}")
    boolean existsById(@Param("projectId") String projectId);
}
