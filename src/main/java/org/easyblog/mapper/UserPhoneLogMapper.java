package org.easyblog.mapper;

import org.easyblog.bean.UserPhoneLog;

public interface UserPhoneLogMapper {
    int deleteByPrimaryKey(Long logId);

    int insert(UserPhoneLog record);

    int insertSelective(UserPhoneLog record);

    UserPhoneLog selectByPrimaryKey(Long logId);

    int updateByPrimaryKeySelective(UserPhoneLog record);

    int updateByPrimaryKeyWithBLOBs(UserPhoneLog record);

    int updateByPrimaryKey(UserPhoneLog record);
}