package top.easyblog.mapper;

import top.easyblog.bean.UserPhoneLog;
import top.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhoneLogMapper extends BaseMapper<UserPhoneLog> {

    int saveSelective(UserPhoneLog record);

    int updateByPrimaryKeySelective(UserPhoneLog record);

    int updateByPrimaryKeyWithContent(UserPhoneLog record);
}