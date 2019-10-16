package org.easyblog.mapper;

import org.easyblog.bean.UserAttention;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAttentionMapper extends BaseMapper<UserAttention> {

    int saveSelective(UserAttention record);

    int updateByPrimaryKeySelective(UserAttention record);

}