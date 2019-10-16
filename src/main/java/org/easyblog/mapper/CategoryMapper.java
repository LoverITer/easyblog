package org.easyblog.mapper;

import org.easyblog.bean.Category;
import org.easyblog.mapper.core.BaseMapper;

public interface CategoryMapper extends BaseMapper<Category> {

    int insertSelective(Category record);

    int updateByPrimaryKeySelective(Category record);

}