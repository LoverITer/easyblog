package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.Category;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

/**
 * @author huangxin
 */
@Repository
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     *
     * @param record
     * @return
     */
    int insertSelective(Category record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     *
     * @param userId
     * @param categoryName
     * @return
     */
    Category getCategoryByUserIdAndName(@Param("userId") int userId, @Param("categoryName") String categoryName);

    /**
     *
     * @param id
     * @return
     */
    List<Category> getSelective(@Param("id") int id);

    /**
     * 得到用户所有不在垃圾桶中的分类
     * @param userId
     * @return
     */
    List<Category> getUserAllCategory(@Param("userId") int userId);

    /**
     * 得到用户所有垃圾桶中的分类
     * @param userId
     * @return
     */
    List<Category> getUserAllDeletedCategory(@Param("userId") int userId);


    /**
     *
     * @param category
     * @return
     */
    int countSelective(@Param("category") Category category);

}