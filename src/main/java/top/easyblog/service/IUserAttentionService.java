package top.easyblog.service;

import top.easyblog.bean.UserAttention;

import java.util.List;

public interface IUserAttentionService {


   /**
    * 选择性的查询用户的关注信息
    * @param userAttention
    * @return
    */
   List<UserAttention> getAllUserAttentionInfo(UserAttention userAttention);

   /**
    * 通过主键删除一个关注信息
    * @param id
    * @return
    */
   int deleteByPK(int id);


   /**
    * 统计我的好友数
    * @param userId
    * @return
    */
   int countAttentionNumOfMe(int userId);

}
