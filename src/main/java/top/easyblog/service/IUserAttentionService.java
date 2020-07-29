package top.easyblog.service;

import top.easyblog.entity.po.UserAttention;

import java.util.List;

/**
 * @author huanxin
 */
public interface IUserAttentionService {


    /**
     * 选择性的查询用户的关注信息
     *
     * @param userAttention UserAttention对象
     * @return List
     */
    List<UserAttention> getAllUserAttentionInfo(UserAttention userAttention);

    /**
     * 通过主键删除一个关注信息
     *
     * @param id UserAttention Id
     * @return int
     */
    int deleteByPK(int id);


    /**
     * 统计我的好友数
     *
     * @param userId 用户Id
     * @return int
     */
    int countAttentionNumOfMe(int userId);

}
