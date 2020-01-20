package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.bean.UserComment;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;


@Repository
public interface UserCommentMapper extends BaseMapper<UserComment> {

    int saveSelective(UserComment record);

    /**
     * 统计接收到的评论数
     * @param receivedId
     * @return
     */
    int countReceivedComment(@Param("receivedUser") int receivedId);

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

    /**
     * 获得关于一篇文章的所有父级评论
     * @param articleId
     * @return
     */
    List<UserComment> getTopCommentsByArticleId(int articleId);

    /**
     *根据PID和主键获得评论
     * @param articleId
     * @param id
     * @return
     */
    List<UserComment> getByPidAndPrimaryKey(int articleId, int id);

}