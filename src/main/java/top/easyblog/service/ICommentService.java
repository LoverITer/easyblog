package top.easyblog.service;

import com.github.pagehelper.PageInfo;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.entity.po.UserComment;

import java.util.List;

/**
 * @author huanxin
 */
public interface ICommentService {

    /**
     * 添加一条评论信息
     *
     * @param comment UserComment
     * @return int
     */
    int save(UserComment comment);

    /**
     * 根据被评论者的用户Id获得用户的评论数
     *
     * @param receivedUserId 被评论者的用户Id
     * @return int
     */
    int getReceiveCommentNum(int receivedUserId);

    /**
     * 获得评论
     *
     * @param userId 用户id
     * @param flag   标志 当为receive时查询用户获得的评论  为send查询用户发表过的评论
     * @return List
     */
    List<UserComment> getComment(int userId, String flag);

    /**
     * 获得评论并且分页
     *
     * @param userId    用户id
     * @param flag      标志 当为receive时查询用户获得的评论  为send查询用户发表过的评论
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<UserComment> getCommentPage(int userId, String flag, PageParam pageParam);


    /**
     * 删除一条评论
     *
     * @param commentId 评论Id
     * @return int
     */
    int deleteComment(int commentId);

    /**
     * 得到一篇文章的所有评论
     *
     * @param articleId 文章Id
     * @return List
     */
    List<UserComment> getArticleComments(long articleId);

}
