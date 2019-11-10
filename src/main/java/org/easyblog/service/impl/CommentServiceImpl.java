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


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "comments",condition = "#result!=null&&#result.size()>0")
    @Override
    public List<UserComment> getComment(int userId,String flag) {

        if(userId>0){
            try{
                List<UserComment> comments=null;
                HashMap<String, String> map = new HashMap<>();
                if("receive".equals(flag)) {
                    comments= commentMapper.getReceiveComment(userId);
                    if(Objects.nonNull(comments)) {
                        comments.forEach(ele -> {
                            Article article = articleService.getByPrimaryKey(ele.getArticleId());
                            map.put("article", article.getArticleTopic());
                            User user = userMapper.getByPrimaryKey(Long.valueOf(ele.getCommentSend()));
                            map.put("userName", user.getUserNickname());
                            ele.setInfo(map);
                        });
                    }
                }else if ("send".equals(flag)){
                    comments= commentMapper.getSendComment(userId);
                    if(Objects.nonNull(comments)) {
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
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public int deleteComment(int commentId) {
        if(commentId>0){
            try {
                return commentMapper.deleteByPrimaryKey((long) commentId);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
}
