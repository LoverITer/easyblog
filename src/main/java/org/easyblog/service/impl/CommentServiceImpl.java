package org.easyblog.service.impl;

import org.easyblog.bean.UserComment;
import org.easyblog.mapper.UserCommentMapper;
import org.easyblog.service.ICommentService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements ICommentService {

    private final UserCommentMapper commentMapper;

    public CommentServiceImpl(UserCommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "comments",condition = "#result!=null&&#result.size()>0")
    @Override
    public List<UserComment> getComment(int userId,String flag) {

        if(userId>0){
            try{
                if("receive".equals(flag)) {
                    return commentMapper.getReceiveComment(userId);
                }else if ("send".equals(flag)){
                    return commentMapper.getSendComment(userId);
                }
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
