package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.mapper.core.BaseMapper;
import top.easyblog.oauth2.bean.Oauth;

import java.util.List;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/28 15:51
 */
@Repository
public interface OauthMapper extends BaseMapper<Oauth> {

    /**
     * 通过主键oauthId删除记录
     *
     * @param oauthId 主键
     * @return
     */
    int deleteByPrimaryKey(Integer oauthId);


    /**
     * 字段不为空就插入
     *
     * @param record
     * @return
     */
    int insertSelective(Oauth record);

    Oauth selectByPrimaryKey(Integer oauthId);

    int updateByPrimaryKeySelective(Oauth record);

    int updateByPrimaryKeyWithBLOBs(Oauth record);

    /**
     * 字段值不为空就作为查询条件
     *
     * @param oauth 查询的条件
     * @return 符合条件的一个Oauth记录
     */
    List<Oauth> getUserSelective(@Param(value = "oauth") Oauth oauth);

}