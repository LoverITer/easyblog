package org.easyblog.service;

import org.easyblog.bean.Category;
import org.easyblog.mapper.CategoryMapper;
import org.easyblog.service.base.ICategoryService;
import org.easyblog.utils.FileUploadUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@CacheConfig(keyGenerator = "keyGenerator", cacheManager = "cacheManager")
@Repository
public class CategoryServiceImpl implements ICategoryService {


    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "getUserAllCategory", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Category> getUserAllViableCategory(int userId) {
        List<Category> categories = categoryMapper.getSelective(userId);
        categories.forEach(ele -> {
            if (ele.getCategoryImageUrl() == null || "".equals(ele.getCategoryImageUrl())) {
                ele.setCategoryImageUrl(FileUploadUtils.defaultCategoryImage());
            }
        });
        return categories;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "getCategory", condition = "#result!=null")
    @Override
    public Category getCategory(int categoryId) {
        if (categoryId > 0) {
            return categoryMapper.getByPrimaryKey((long) categoryId);
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CachePut(cacheNames = "saveCategory", condition = "#result>0")
    @Override
    public int saveCategory(int userId, String categoryName) {
        if (userId > 0 && categoryName != null) {
            Category category = new Category(userId, categoryName, FileUploadUtils.defaultCategoryImage(), 0, 0, 0, 1);
            return categoryMapper.insertSelective(category);
        }
        return -1;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CachePut(cacheNames = "updateCategoryInfo", condition = "#result!=null")
    @Override
    public boolean updateCategoryInfo(int categoryId, Map<String, Object> params) {
        if (categoryId < 0 || params == null) {
            return false;
        }
        Category category = new Category();
        category.setCategoryId((long) categoryId);
        params.forEach((k, v) -> {
            if ("categoryName".equals(k)) {
                category.setCategoryName((String) v);
            } else if ("categoryImageUrl".equals(k)) {
                category.setCategoryImageUrl((String) v);
            } else if ("categoryArticleNum".equals(k)) {
                category.setCategoryArticleNum((Integer) v);
            } else if ("categoryClickNum".equals(k)) {
                category.setCategoryClickNum((Integer) v);
            } else if ("categoryCareNum".equals(k)) {
                category.setCategoryCareNum((Integer) v);
            } else if ("display".equals(k)) {
                category.setDisplay((Integer) v);
            }
        });
        try {
            categoryMapper.updateByPrimaryKeySelective(category);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
