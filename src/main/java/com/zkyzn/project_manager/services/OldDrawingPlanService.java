package com.zkyzn.project_manager.services;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.OldDrawingPlanDao;
import com.zkyzn.project_manager.models.OldDrawingPlan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OldDrawingPlanService extends MPJBaseServiceImpl<OldDrawingPlanDao, OldDrawingPlan> {

    /**
     * 获取所有
     *
     * @return 返回所有图纸计划
     */
    public List<OldDrawingPlan> list() {
        MPJLambdaQueryWrapper<OldDrawingPlan> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(OldDrawingPlan.class);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据图纸计划名称获取图纸计划
     * @param key 图纸计划名称
     * @return 返回图纸计划
     */
    public OldDrawingPlan getByKey(String key) {
        MPJLambdaQueryWrapper<OldDrawingPlan> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.like(OldDrawingPlan::getDrawingPlanName, key);
        queryWrapper.selectAll(OldDrawingPlan.class);
        return baseMapper.selectOne(queryWrapper);
    }
}
