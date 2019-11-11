package org.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.easyblog.bean.UserAttention;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttentionMapper extends BaseMapper<UserAttention> {

    int saveSelective(UserAttention record);

    int updateByPrimaryKeySelective(UserAttention record);

    List<UserAttention> getUserAllAttentionInfoSelective(@Param("userAttention") UserAttention userAttention);

}