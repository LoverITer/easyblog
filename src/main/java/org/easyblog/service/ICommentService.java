package org.easyblog.service;

import org.easyblog.bean.UserComment;

import java.util.List;

public interface ICommentService {

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




}
