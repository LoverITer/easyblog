package org.easyblog.mapper;

import org.easyblog.bean.UserPower;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPowerMapper extends BaseMapper<UserPower> {

    int saveSelective(UserPower record);

    int updateByPrimaryKeySelective(UserPower record);

}