package org.easyblog.mapper;

import org.easyblog.bean.UserAttention;

public interface UserAttentionMapper {
    int deleteByPrimaryKey(Short id);

    int insert(UserAttention record);

    int insertSelective(UserAttention record);

    UserAttention selectByPrimaryKey(Short id);

    int updateByPrimaryKeySelective(UserAttention record);

    int updateByPrimaryKey(UserAttention record);
}