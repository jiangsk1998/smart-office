package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zkyzn.project_manager.mappers.UserInfoDao;
import com.zkyzn.project_manager.models.Department;
import com.zkyzn.project_manager.models.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService extends MPJBaseServiceImpl<UserInfoDao, UserInfo> {

    /**
     * 通过用户Id获取用户信息
     * @param userId 用户Id
     * @return 用户信息
     */
    public UserInfo GetByUserId(Long userId){
        return baseMapper.selectById(userId);
    }

    /**
     * 通过用户账号获取用户信息
     * @param userAccount 用户账号
     * @return 用户信息
     */
    public UserInfo GetByUserAccount(String userAccount){
        MPJLambdaQueryWrapper<UserInfo> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(UserInfo.class)
                .eq(UserInfo::getUserAccount,userAccount);
        return baseMapper.selectOne(queryWrapper);
    }

    // 分页查询接口
    public IPage<UserInfo> page(int current, int size, String userName) {
        Page<UserInfo> page = new Page<>(current, size);
        MPJLambdaWrapper<UserInfo> wrapper = new MPJLambdaWrapper<>();
        wrapper = wrapper.selectAll(UserInfo.class)
                .selectAs(Department::getName, "department_name")
                .leftJoin(Department.class, on -> on.eq(Department::getId, UserInfo::getDepartmentId));

        if (StringUtils.isNotBlank(userName)) {
            wrapper.like(UserInfo::getUserName, userName);
        }

        return baseMapper.selectPage(page, wrapper);
    }


    public UserInfo getUserInfoByName(String userName) {
        MPJLambdaQueryWrapper<UserInfo> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(UserInfo.class)
                .eq(UserInfo::getUserName,userName);
        return baseMapper.selectOne(queryWrapper);
    }
}
