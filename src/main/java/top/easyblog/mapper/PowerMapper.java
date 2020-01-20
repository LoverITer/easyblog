package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.bean.Power;
import top.easyblog.mapper.core.BaseMapper;

@Repository
public interface PowerMapper extends BaseMapper<Power> {

    int saveSelective(Power record);

    int updateByPrimaryKeySelective(Power record);

}