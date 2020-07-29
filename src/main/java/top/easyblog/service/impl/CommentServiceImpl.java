package top.easyblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.common.exception.IllegalPageParameterException;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserComment;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.UserCommentMapper;
import top.easyblog.mapper.UserMapper;
import top.easyblog.service.ICommentService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
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

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "comments", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<UserComment> getComment(int userId, String flag) {
        if (userId > 0) {
            try {
                List<UserComment> comments = null;
                if ("receive".equals(flag)) {
                    comments = commentMapper.getReceiveComment(userId);
                    if (Objects.nonNull(comments)) {
                        comments.forEach(ele -> {
                            mapInfo(ele, ele.getCommentSend());
                        });
                    }
                } else if ("send".equals(flag)) {
                    comments = commentMapper.getSendComment(userId);
                    if (Objects.nonNull(comments)) {
                        comments.forEach(ele -> {
                            mapInfo(ele, ele.getCommentReceived());
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

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor = Exception.class)
    @Override
    public PageInfo<UserComment> getCommentPage(int userId, String flag, PageParam pageParam) {
        PageInfo<UserComment> pageInfo=null;
        if(userId>0&& StringUtil.isNotEmpty(flag)){
            if(Objects.nonNull(pageParam)){
                try {
                    if ("receive".equals(flag)) {
                        PageHelper.startPage(pageParam.getPage(),pageParam.getPageSize());
                        List<UserComment> comments = commentMapper.getReceiveComment(userId);
                        if (Objects.nonNull(comments)) {
                            comments.forEach(ele -> mapInfo(ele, ele.getCommentSend()));
                        }
                        pageInfo=new PageInfo<>(comments);
                    } else if ("send".equals(flag)) {
                        PageHelper.startPage(pageParam.getPage(),pageParam.getPageSize());
                        List<UserComment>  comments = commentMapper.getSendComment(userId);
                        if (Objects.nonNull(comments)) {
                            comments.forEach(ele -> mapInfo(ele, ele.getCommentReceived()));
                        }
                        pageInfo=new PageInfo<>(comments);
                    }
                } catch (Exception e) {
                   throw new RuntimeException(e.getCause());
                }
            }else{
                throw new IllegalPageParameterException();
            }
        }
        return pageInfo;
    }

    private void mapInfo(UserComment userComment, Integer commentReceived) {
        try {
            HashMap<String, String> map = new HashMap<>();
            Article article = articleService.getByPrimaryKey(userComment.getArticleId());
            map.put("article", article.getArticleTopic());
            User user = userMapper.getByPrimaryKey(Long.valueOf(commentReceived));
            map.put("userName", user.getUserNickname());
            userComment.setInfo(map);
            map = null;
        }catch (Exception e){
            throw new RuntimeException("发生未知异常@CommentService-line 80-90");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor = Exception.class)
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


    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor = Exception.class)
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

    @Override
    public int getReceiveCommentNum(int receivedUserId) {
        if(receivedUserId>0){
            try {
                return commentMapper.countReceivedComment(receivedUserId);
            }catch (Exception e){
                return 0;
            }
        }
        return 0;
    }
}
