package top.easyblog.service;

import com.github.pagehelper.PageInfo;
import top.easyblog.bean.Category;
import top.easyblog.commons.pagehelper.PageParam;

import java.util.List;
import java.util.Map;

public interface ICategoryService {

    /**
     * 保存一条分类信息
     * @param userId
     * @param categoryName
     * @return
     */
    int saveCategory(int userId, String categoryName);


    /**
     * 保存一条分类信息
     * @param category
     * @return
     */
    int saveCategory(Category category);

    /**
     * 获取某个用户的所有允许显示的分类
     * @param userId
     * @return
     */
    List<Category> getUserAllViableCategory(int userId);

    /**
     * 得到用户的所有分类
     * @param userId
     * @return
     */
    List<Category> getUserAllCategories(int userId);

    /**
     * 得到用户的所有分类并且分页
     * @param userId
     * @param pageParam
     * @return
     */
    PageInfo<Category> getUserAllCategoriesPage(int userId, PageParam pageParam);

    /**
     * 根据分类ID获得一个分类记录
     * @param categoryId
     * @return
     */
    Category getCategory(int categoryId);


    Category getCategoryByUserIdAndName(int userId, String categoryName);


    /**
     * 选择性更新分类列表的内容，适用于表中有字段的值要增加的时候
     * @param categoryId
     * @param params
     * @return
     */
    int updateCategoryInfo(int categoryId, Map<String, Object> params);


    /**
     * 根据主键更新
     * @param category
     */
    int  updateByPKSelective(Category category);


    /**
     * 获得用户所有已经删除的分类
     * @param userId
     * @return
     */
    List<Category> getUserAllDeletedCategory(int userId);

    /**
     * 获得用户所有已经删除的分类并且分类
     * @param userId
     * @param pageParam
     * @return
     */
    PageInfo<Category> getUserAllDeletedCategoryPage(int userId,PageParam pageParam);

    /**
     * 选择性的删除
     * @param category
     */
    int deleteCategoryByCondition(Category category);


    /**
     * 选择性的统计分类数量
     * @param category
     * @return
     */
    int countSelective(Category category);


}
