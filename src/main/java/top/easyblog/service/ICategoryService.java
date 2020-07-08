package top.easyblog.service;

import com.github.pagehelper.PageInfo;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.entity.po.Category;

import java.util.List;
import java.util.Map;

/**
 * @author huanxin
 */
public interface ICategoryService {

    /**
     * 保存一条分类信息
     *
     * @param userId       用户Id
     * @param categoryName 文章分类名
     * @return int
     */
    int saveCategory(int userId, String categoryName);


    /**
     * 保存一条分类信息
     *
     * @param category Category对象
     * @return int
     */
    int saveCategory(Category category);

    /**
     * 获取某个用户的所有允许显示的分类
     *
     * @param userId 用户Id
     * @return List
     */
    List<Category> getUserAllViableCategory(int userId);

    /**
     * 得到用户的所有分类
     *
     * @param userId 用户Id
     * @return List
     */
    List<Category> getUserAllCategories(int userId);

    /**
     * 得到用户的所有分类并且分页
     *
     * @param userId    用户Id
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Category> getUserAllCategoriesPage(int userId, PageParam pageParam);

    /**
     * 根据分类ID获得一个分类记录
     *
     * @param categoryId 文章分类Id
     * @return Category对象
     */
    Category getCategory(int categoryId);

    /**
     * 根据用户Id和文章分类名获得分类
     *
     * @param userId       用户Id
     * @param categoryName 文章分类名
     * @return Category对象
     */
    Category getCategoryByUserIdAndName(int userId, String categoryName);


    /**
     * 选择性更新分类列表的内容，适用于表中有字段的值要增加的时候
     *
     * @param categoryId 文章分类Id
     * @param params
     * @return int
     */
    int updateCategoryInfo(int categoryId, Map<String, Object> params);


    /**
     * 根据主键更新
     *
     * @param category Category对象
     */
    int updateByPKSelective(Category category);


    /**
     * 获得用户所有已经删除的分类
     *
     * @param userId 用户Id
     * @return List
     */
    List<Category> getUserAllDeletedCategory(int userId);

    /**
     * 获得用户所有已经删除的分类并且分类
     *
     * @param userId    用户Id
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Category> getUserAllDeletedCategoryPage(int userId, PageParam pageParam);

    /**
     * 选择性的删除
     *
     * @param category Category对象
     * @return int
     */
    int deleteCategoryByCondition(Category category);


    /**
     * 选择性的统计分类数量
     *
     * @param category Category对象
     * @return int
     */
    int countSelective(Category category);


}
