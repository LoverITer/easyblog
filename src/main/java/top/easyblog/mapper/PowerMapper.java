package top.easyblog.mapper;

import top.easyblog.bean.Power;
import top.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerMapper extends BaseMapper<Power> {

    int saveSelective(Power record);

    int updateByPrimaryKeySelective(Power record);

}