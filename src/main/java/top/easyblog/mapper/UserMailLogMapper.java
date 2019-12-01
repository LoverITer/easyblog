package top.easyblog.mapper;

import top.easyblog.bean.UserMailLog;
import top.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMailLogMapper extends BaseMapper<UserMailLog> {

    int saveSelective(UserMailLog record);

    int updateByPrimaryKeySelective(UserMailLog record);

    int updateByPrimaryKeyWithContent(UserMailLog record);

}