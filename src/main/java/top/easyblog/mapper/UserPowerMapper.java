package top.easyblog.mapper;

import top.easyblog.bean.UserPower;
import top.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPowerMapper extends BaseMapper<UserPower> {

    int saveSelective(UserPower record);

    int updateByPrimaryKeySelective(UserPower record);

}