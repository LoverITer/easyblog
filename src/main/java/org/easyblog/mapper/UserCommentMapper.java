package org.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.easyblog.bean.UserComment;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserCommentMapper extends BaseMapper<UserComment> {

    int saveSelective(UserComment record);

    int updateByPrimaryKeySelective(UserComment record);

    int updateByPrimaryKeyWithContent(UserComment record);

    /**
     * 得到所有该用户对别人的文章评论
     * @param receiveUser
     * @return
     */
    List<UserComment> getReceiveComment(@Param("receiveUser") int receiveUser);


    /**
     * 得到所有关于该用户的文章评论
     * @param receiveUser
     * @return
     */
    List<UserComment> getSendComment(@Param("sendUser") int receiveUser);

}