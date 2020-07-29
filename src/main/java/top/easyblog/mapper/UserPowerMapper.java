package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.UserPower;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author huangxin
 */
@Repository
public interface UserPowerMapper extends BaseMapper<UserPower> {
    /**
     * @param record
     * @return
     */
    int saveSelective(UserPower record);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserPower record);

}