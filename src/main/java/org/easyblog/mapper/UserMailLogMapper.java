package org.easyblog.mapper;

import org.easyblog.bean.UserMailLog;

public interface UserMailLogMapper {
    int deleteByPrimaryKey(Long logId);

    int insert(UserMailLog record);

    int insertSelective(UserMailLog record);

    UserMailLog selectByPrimaryKey(Long logId);

    int updateByPrimaryKeySelective(UserMailLog record);

    int updateByPrimaryKeyWithBLOBs(UserMailLog record);

    int updateByPrimaryKey(UserMailLog record);
}