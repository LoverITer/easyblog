package org.easyblog.mapper;

import org.easyblog.bean.UserPower;

public interface UserPowerMapper {
    int deleteByPrimaryKey(Byte powerId);

    int insert(UserPower record);

    int insertSelective(UserPower record);

    UserPower selectByPrimaryKey(Byte powerId);

    int updateByPrimaryKeySelective(UserPower record);

    int updateByPrimaryKey(UserPower record);
}