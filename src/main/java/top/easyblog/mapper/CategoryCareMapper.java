package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import top.easyblog.bean.CategoryCare;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

public interface CategoryCareMapper extends BaseMapper<CategoryCare> {

    List<CategoryCare> getCategoryCareByCategoryId(@Param("categoryId") int categoryId);

    void deleteByUserIdAndCategoryId(@Param("userId") Integer userId,@Param("categoryId") Integer categoryId);

}
