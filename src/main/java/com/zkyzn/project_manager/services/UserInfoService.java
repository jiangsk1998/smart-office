package com.zkyzn.project_manager.services;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.UserInfoDao;
import com.zkyzn.project_manager.models.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService extends MPJBaseServiceImpl<UserInfoDao, UserInfo> {

    /**
     * 通过用户Id获取用户信息
     * @param userId 用户Id
     * @return 用户信息
     */
    public UserInfo GetByUserId(Integer userId){
        return baseMapper.selectById(userId);
    }

    /**
     * 通过用户账号获取用户信息
     * @param userAccount 用户账号
     * @return 用户信息
     */
    public UserInfo GetByUserAccount(String userAccount){
        MPJLambdaQueryWrapper<UserInfo> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserAccount,userAccount);
        return baseMapper.selectOne(queryWrapper);
    }
}
