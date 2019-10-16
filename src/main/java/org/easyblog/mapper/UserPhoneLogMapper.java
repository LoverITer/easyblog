package org.easyblog.mapper;

import org.easyblog.bean.UserPhoneLog;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhoneLogMapper extends BaseMapper<UserPhoneLog> {

    int saveSelective(UserPhoneLog record);

    int updateByPrimaryKeySelective(UserPhoneLog record);

    int updateByPrimaryKeyWithContent(UserPhoneLog record);
}