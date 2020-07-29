package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.UserMailLog;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author huangxin
 */
@Repository
public interface UserMailLogMapper extends BaseMapper<UserMailLog> {
    /**
     *
     * @param record
     * @return
     */
    int saveSelective(UserMailLog record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserMailLog record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithContent(UserMailLog record);

}