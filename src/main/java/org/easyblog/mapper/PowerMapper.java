package org.easyblog.mapper;

import org.easyblog.bean.Power;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerMapper extends BaseMapper<Power> {

    int saveSelective(Power record);

    int updateByPrimaryKeySelective(Power record);

}