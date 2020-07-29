package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.UserPhoneLog;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author huangxin
 */
@Repository
public interface UserPhoneLogMapper extends BaseMapper<UserPhoneLog> {
    /**
     * @param record
     * @return
     */
    int saveSelective(UserPhoneLog record);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserPhoneLog record);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithContent(UserPhoneLog record);
}