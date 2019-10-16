package org.easyblog.mapper;

import org.easyblog.bean.UserSigninLog;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSigninLogMapper extends BaseMapper<UserSigninLog> {

    int saveSelective(UserSigninLog record);

    int updateByPrimaryKeySelective(UserSigninLog record);
}