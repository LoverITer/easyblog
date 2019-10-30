package org.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.easyblog.bean.Category;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper extends BaseMapper<Category> {

    int insertSelective(Category record);

    int updateByPrimaryKeySelective(Category record);

    List<Category> getSelective(@Param("id") int id);


    List<Category> getUserAllCategory(@Param("userId") int userId);

}