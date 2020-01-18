package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.bean.UserMailLog;
import top.easyblog.mapper.core.BaseMapper;

@Repository
public interface UserMailLogMapper extends BaseMapper<UserMailLog> {

    int saveSelective(UserMailLog record);

    int updateByPrimaryKeySelective(UserMailLog record);

    int updateByPrimaryKeyWithContent(UserMailLog record);

}