package org.easyblog.mapper;

import org.easyblog.bean.Power;

public interface PowerMapper {
    int deleteByPrimaryKey(Byte powerId);

    int insert(Power record);

    int insertSelective(Power record);

    Power selectByPrimaryKey(Byte powerId);

    int updateByPrimaryKeySelective(Power record);

    int updateByPrimaryKey(Power record);
}