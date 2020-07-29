package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.Power;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author huangxin
 */
@Repository
public interface PowerMapper extends BaseMapper<Power> {
    /**
     *
     * @param record
     * @return
     */
    int saveSelective(Power record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Power record);

}