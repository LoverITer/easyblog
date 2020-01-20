package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.bean.UserPhoneLog;
import top.easyblog.mapper.core.BaseMapper;

@Repository
public interface UserPhoneLogMapper extends BaseMapper<UserPhoneLog> {

    int saveSelective(UserPhoneLog record);

    int updateByPrimaryKeySelective(UserPhoneLog record);

    int updateByPrimaryKeyWithContent(UserPhoneLog record);
}