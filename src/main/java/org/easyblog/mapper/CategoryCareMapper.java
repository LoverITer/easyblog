package org.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.easyblog.bean.CategoryCare;
import org.easyblog.mapper.core.BaseMapper;

import java.util.List;

public interface CategoryCareMapper extends BaseMapper<CategoryCare> {

    List<CategoryCare> getCategoryCareByCategoryId(@Param("categoryId") int categoryId);



}
