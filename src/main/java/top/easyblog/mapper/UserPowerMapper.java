package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.bean.UserPower;
import top.easyblog.mapper.core.BaseMapper;

@Repository
public interface UserPowerMapper extends BaseMapper<UserPower> {

    int saveSelective(UserPower record);

    int updateByPrimaryKeySelective(UserPower record);

}