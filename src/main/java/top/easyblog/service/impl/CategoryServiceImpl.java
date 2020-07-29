package top.easyblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.common.exception.IllegalPageParameterException;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.entity.po.Category;
import top.easyblog.mapper.CategoryMapper;
import top.easyblog.service.ICategoryService;
import top.easyblog.util.DefaultImageDispatcherUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangxin
 */
@CacheConfig(keyGenerator = "keyGenerator", cacheManager = "cacheManager")
@Service
public class CategoryServiceImpl implements ICategoryService {


    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "getUserAllCategory", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Category> getUserAllViableCategory(int userId) {
        List<Category> categories = categoryMapper.getSelective(userId);
        categories.forEach(ele -> {
            if (ele.getCategoryImageUrl() == null || "".equals(ele.getCategoryImageUrl())) {
                ele.setCategoryImageUrl(DefaultImageDispatcherUtils.defaultCategoryImage());
            }
        });
        return categories;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "category", condition = "#result!=null")
    @Override
    public Category getCategory(int categoryId) {
        if (categoryId > 0) {
            return categoryMapper.getByPrimaryKey((long) categoryId);
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "category",condition = "#result!=null")
    @Override
    public Category getCategoryByUserIdAndName(int userId, String categoryName) {
        if(userId>0&&!"".equals(categoryName)){
            try {
                return categoryMapper.getCategoryByUserIdAndName(userId, categoryName);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @CachePut(cacheNames = "category", condition = "#result>0")
    @Override
    public int saveCategory(int userId, String categoryName) {
        if (userId > 0 && categoryName != null) {
            Category category = new Category(userId, categoryName, DefaultImageDispatcherUtils.defaultCategoryImage(), 0, 0, 0, "1","");
            return categoryMapper.insertSelective(category);
        }
        return -1;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @CachePut(cacheNames = "category", condition = "#result>0")
    @Override
    public int saveCategory(Category category) {
        if (Objects.nonNull(category)) {
            try {
                 return categoryMapper.save(category);
            }catch (Exception e){
                return 0;
            }
        }
        return 0;
    }



    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "categories",condition = "#result!=null&&#reslut.size>0")
    @Override
    public List<Category> getUserAllCategories(int userId) {
        if(userId>0) {
            try {
                return categoryMapper.getUserAllCategory(userId);
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public PageInfo<Category> getUserAllCategoriesPage(int userId, PageParam pageParam) {
        PageInfo<Category> pageInfo=null;
        if(userId>0){
            if(Objects.nonNull(pageParam)){
                try {
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Category> categories = categoryMapper.getUserAllCategory(userId);
                    pageInfo = new PageInfo<>(categories);
                }catch (Exception e){
                    throw new RuntimeException(e.getCause());
                }
            }else{
                throw new IllegalPageParameterException();
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "categories",condition = "#result!=null&&#reslut.size>0")
    @Override
    public List<Category> getUserAllDeletedCategory(int userId) {
        if(userId>0){
            try {
                return categoryMapper.getUserAllDeletedCategory(userId);
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public PageInfo<Category> getUserAllDeletedCategoryPage(int userId, PageParam pageParam) {
        PageInfo<Category> pageInfo=null;
        if(userId>0){
            if (Objects.nonNull(pageParam)) {
                try{
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Category> categories = categoryMapper.getUserAllDeletedCategory(userId);
                    pageInfo = new PageInfo<>(categories);
                }catch (Exception e){
                    throw new RuntimeException(e.getCause());
                }
            }else{
                throw new IllegalPageParameterException();
            }
        }
        return pageInfo;
    }

    @Transactional(isolation=Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @CachePut(cacheNames = "category", condition = "#result>0")
    @Override
    public int updateByPKSelective(Category category) {
        if(Objects.nonNull(category)){
            try {
                return categoryMapper.updateByPrimaryKeySelective(category);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @CachePut(cacheNames = "category", condition = "#result>0")
    @Override
    public int updateCategoryInfo(int categoryId, Map<String, Object> params) {
        if (categoryId < 0 || params == null) {
            return 0;
        }
        Category category = new Category();
        category.setCategoryId(categoryId);
        params.forEach((k, v) -> {
            if ("categoryName".equals(k)) {
                category.setCategoryName((String) v);
            }
            if ("categoryImageUrl".equals(k)) {
                category.setCategoryImageUrl((String) v);
            }
            if ("categoryArticleNum".equals(k)) {
                category.setCategoryArticleNum((Integer) v);
            }
            if ("categoryClickNum".equals(k)) {
                category.setCategoryClickNum((Integer) v);
            }
            if ("categoryCareNum".equals(k)) {
                category.setCategoryCareNum((Integer) v);
            }
            if ("display".equals(k)) {
                category.setDisplay(v+"");
            }
        });
        try {
            return categoryMapper.updateByPrimaryKeySelective(category);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @CacheEvict(cacheNames = "category",condition = "#result>0")
    @Override
    public int deleteCategoryByCondition(Category category) {
        if(Objects.nonNull(category)) {
            try {
                return categoryMapper.deleteSelective(category);
            }catch (Exception e){
                return 0;
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public int countSelective(Category category) {
        if(Objects.nonNull(category)){
            try{
                return categoryMapper.countSelective(category);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
}
