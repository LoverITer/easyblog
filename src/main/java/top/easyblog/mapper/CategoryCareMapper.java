package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import top.easyblog.entity.po.CategoryCare;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

/**
 * @author huangxin
 */
public interface CategoryCareMapper extends BaseMapper<CategoryCare> {
    /**
     *
     * @param categoryId
     * @return
     */
    List<CategoryCare> getCategoryCareByCategoryId(@Param("categoryId") int categoryId);

    /**
     *
     * @param userId
     * @param categoryId
     */
    void deleteByUserIdAndCategoryId(@Param("userId") Integer userId, @Param("categoryId") Integer categoryId);

}
