package top.easyblog.service;

import top.easyblog.bean.UserComment;

import java.util.List;

public interface ICommentService {

    /**
     * 添加一条评论信息
     * @param comment
     * @return
     */
    int save(UserComment comment);


    int getReceiveCommentNum(int receivedUserId);

    /**
     * 获得评论
     * @param userId  用户id
     * @param flag   标志 当为receive时查询用户获得的评论  为send查询用户发表过的评论
     * @return
     */
    List<UserComment> getComment(int userId,String flag);


    /**
     * 删除一条评论
     * @param commentId
     * @return
     */
    int deleteComment(int commentId);

    /**
     * 得到一篇文章的所有评论
     * @param articleId
     * @return
     */
    List<UserComment> getArticleComments(long articleId);

}
