package org.easyblog.mapper;

import org.easyblog.bean.UserComment;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;


@Repository
public interface UserCommentMapper extends BaseMapper<UserComment> {

    int saveSelective(UserComment record);

    int updateByPrimaryKeySelective(UserComment record);

    int updateByPrimaryKeyWithContent(UserComment record);
}