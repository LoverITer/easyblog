package org.easyblog.mapper;

import org.easyblog.bean.UserSignInLog;

public interface UserSignInLogMapper {
    int deleteByPrimaryKey(Long logId);

    int insert(UserSignInLog record);

    int insertSelective(UserSignInLog record);

    UserSignInLog selectByPrimaryKey(Long logId);

    int updateByPrimaryKeySelective(UserSignInLog record);

    int updateByPrimaryKey(UserSignInLog record);
}