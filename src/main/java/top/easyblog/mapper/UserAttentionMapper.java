package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.bean.UserAttention;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

@Repository
public interface UserAttentionMapper extends BaseMapper<UserAttention> {

    int saveSelective(UserAttention record);

    int updateByPrimaryKeySelective(UserAttention record);

    List<UserAttention> getUserAllAttentionInfoSelective(@Param("userAttention") UserAttention userAttention);

    int countAttentionNumSelective(@Param("userAttention") UserAttention attention);

}