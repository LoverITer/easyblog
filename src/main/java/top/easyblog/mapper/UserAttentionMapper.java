package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.UserAttention;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

/**
 * @author huangxin
 */
@Repository
public interface UserAttentionMapper extends BaseMapper<UserAttention> {
    /**
     *
     * @param record
     * @return
     */
    int saveSelective(UserAttention record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserAttention record);

    /**
     *
     * @param userAttention
     * @return
     */
    List<UserAttention> getUserAllAttentionInfoSelective(@Param("userAttention") UserAttention userAttention);

    /**
     *
     * @param attention
     * @return
     */
    int countAttentionNumSelective(@Param("userAttention") UserAttention attention);

}