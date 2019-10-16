package org.easyblog.mapper;

import org.easyblog.bean.UserComment;

public interface UserCommentMapper {
    int deleteByPrimaryKey(Integer commentId);

    int insert(UserComment record);

    int insertSelective(UserComment record);

    UserComment selectByPrimaryKey(Integer commentId);

    int updateByPrimaryKeySelective(UserComment record);

    int updateByPrimaryKeyWithBLOBs(UserComment record);

    int updateByPrimaryKey(UserComment record);
}