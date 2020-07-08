package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.UserComment;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

/**
 * @author huangxin
 */
@Repository
public interface UserCommentMapper extends BaseMapper<UserComment> {
    /**
     * @param record
     * @return
     */
    int saveSelective(UserComment record);

    /**
     * 统计接收到的评论数
     *
     * @param receivedId
     * @return
     */
    int countReceivedComment(@Param("receivedUser") int receivedId);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserComment record);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithContent(UserComment record);

    /**
     * 得到所有该用户对别人的文章评论
     *
     * @param receiveUser
     * @return
     */
    List<UserComment> getReceiveComment(@Param("receiveUser") int receiveUser);


    /**
     * 得到所有关于该用户的文章评论
     *
     * @param receiveUser
     * @return
     */
    List<UserComment> getSendComment(@Param("sendUser") int receiveUser);

    /**
     * 获得关于一篇文章的所有父级评论
     *
     * @param articleId 文章Id
     * @return 一篇文章的所有父级评论
     */
    List<UserComment> getTopCommentsByArticleId(@Param(value = "articleId") int articleId);

    /**
     * 根据父级评论id(pid)和文章Id主键获得关于一篇文章的所有评论
     *
     * @param articleId 文章Id
     * @param pid       父评论Id
     * @return 一篇文章的所有父级评论下的子评论
     */
    List<UserComment> getByPidAndPrimaryKey(@Param(value = "articleId") int articleId, @Param(value = "pid") int pid);

}