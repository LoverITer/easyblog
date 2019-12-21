package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import top.easyblog.bean.Category;
import top.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper extends BaseMapper<Category> {

    int insertSelective(Category record);

    int updateByPrimaryKeySelective(Category record);

    Category getCategoryByUserIdAndName(@Param("userId") int userId, @Param("categoryName") String categoryName);

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

    int countSelective(@Param("category") Category category);

}