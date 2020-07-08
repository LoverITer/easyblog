package top.easyblog.service.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.entity.po.CategoryCare;
import top.easyblog.mapper.CategoryCareMapper;
import top.easyblog.service.ICategoryCareService;

import java.util.List;


/**
 * @author huangxin
 */
@CacheConfig(keyGenerator = "keyGenerator", cacheManager = "cacheManager")
@Service
public class CategoryCareServiceImpl implements ICategoryCareService {

    private final CategoryCareMapper categoryCareMapper;

    public CategoryCareServiceImpl(CategoryCareMapper categoryCareMapper) {
        this.categoryCareMapper = categoryCareMapper;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "getCategoryCare", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<CategoryCare> getCategoryCare(int categoryId) {
       return categoryCareMapper.getCategoryCareByCategoryId(categoryId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Cacheable(cacheNames = "saveCareInfo", condition = "#result>0")
    @Override
    public int saveCareInfo(int careUserId, int categoryId) {
        CategoryCare categoryCare = new CategoryCare(categoryId, careUserId);
        try {
            return categoryCareMapper.save(categoryCare);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public boolean deleteCareInfo(int userId, int categoryId) {
        try {
            categoryCareMapper.deleteByUserIdAndCategoryId(userId, categoryId);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
