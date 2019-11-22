package org.easyblog.service.impl;

import org.easyblog.bean.Article;
import org.easyblog.bean.User;
import org.easyblog.bean.UserComment;
import org.easyblog.mapper.ArticleMapper;
import org.easyblog.mapper.UserCommentMapper;
import org.easyblog.mapper.UserMapper;
import org.easyblog.service.ICommentService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl implements ICommentService {

    private final UserCommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleService;

    public CommentServiceImpl(UserCommentMapper commentMapper, UserMapper userMapper, ArticleMapper articleService) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.articleService = articleService;
    }

    @Override
    public int save(UserComment comment) {
        if (Objects.nonNull(comment)) {
            try {
                return commentMapper.save(comment);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "comments", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<UserComment> getComment(int userId, String flag) {
        if (userId > 0) {
            try {
                List<UserComment> comments = null;
                HashMap<String, String> map = new HashMap<>();
                if ("receive".equals(flag)) {
                    comments = commentMapper.getReceiveComment(userId);
                    if (Objects.nonNull(comments)) {
                        comments.forEach(ele -> {
                            Article article = articleService.getByPrimaryKey(ele.getArticleId());
                            map.put("article", article.getArticleTopic());
                            User user = userMapper.getByPrimaryKey(Long.valueOf(ele.getCommentSend()));
                            map.put("userName", user.getUserNickname());
                            ele.setInfo(map);
                        });
                    }
                } else if ("send".equals(flag)) {
                    comments = commentMapper.getSendComment(userId);
                    if (Objects.nonNull(comments)) {
                        comments.forEach(ele -> {
                            Article article = articleService.getByPrimaryKey(ele.getArticleId());
                            map.put("article", article.getArticleTopic());
                            User user = userMapper.getByPrimaryKey(Long.valueOf(ele.getCommentReceived()));
                            map.put("userName", user.getUserNickname());
                            ele.setInfo(map);
                        });
                    }
                }
                return comments;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    @Override
    public int deleteComment(int commentId) {
        if (commentId > 0) {
            try {
                return commentMapper.deleteByPrimaryKey((long) commentId);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }


    @Transactional
    @Cacheable(cacheNames = "comments", condition = "#result!=null&&result.size()>0")
    @Override
    public List<UserComment> getArticleComments(long articleId) {
        if (articleId > 0) {
            List<UserComment> parentComments = commentMapper.getTopCommentsByArticleId((int) articleId);
            parentComments.forEach(element -> {
                List<UserComment> lists = new ArrayList<>();
                List<UserComment> childComments = null;
                try {
                    childComments = getChildComments(element, (int) articleId, lists);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                element.setChildComment(childComments);
                //获得评论者的信息
                getCommenterInfo(element.getCommentSend(), -1, element);
            });
            return parentComments;
        }
        return null;
    }

    /**
     * 获得子评论
     *
     * @param parentComments 父级评论
     * @param articleId      文章Id
     * @param allChildren    子评论统一放到一个List集合中
     * @return
     * @throws SQLException
     */
    private List<UserComment> getChildComments(UserComment parentComments, int articleId, List<UserComment> allChildren) throws SQLException {
        //尝试获得子评论
        List<UserComment> comments = commentMapper.getByPidAndPrimaryKey(articleId, parentComments.getCommentId());
            if (Objects.nonNull(comments)) {
                comments.forEach(comment -> {
                    allChildren.add(comment);
                    try {
                        getChildComments(comment, articleId, allChildren);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //获得评论者的信息
                    getCommenterInfo(comment.getCommentSend(), comment.getCommentReceived(), comment);
                });
                return allChildren;
            }
        return null;
    }

    /**
     * 给获得每条评论信息中评论双方的信息
     *
     * @param SendUserId     发送评论人的信息
     * @param receivedUserId 接收评论的人的信息
     * @param comment        评论对象
     */
    private void getCommenterInfo(int SendUserId, int receivedUserId, UserComment comment) {
        if (SendUserId > 0) {
            try {
                User sendUser = userMapper.getByPrimaryKey((long) SendUserId);
                User receiveUser = null;
                if (receivedUserId > 0) {
                    receiveUser = userMapper.getByPrimaryKey((long) receivedUserId);
                }
                if (Objects.nonNull(sendUser)) {
                    comment.setSendUserNickname(sendUser.getUserNickname());
                    comment.setSendUserNicknameHeadImgUrl(sendUser.getUserHeaderImgUrl());
                    if (Objects.nonNull(receiveUser)) {
                        comment.setReceivedUserNickname(receiveUser.getUserNickname());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
