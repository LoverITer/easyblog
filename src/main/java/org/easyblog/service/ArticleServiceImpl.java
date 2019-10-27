package org.easyblog.service;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;
import org.easyblog.mapper.ArticleMapper;
import org.easyblog.service.base.IArticleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@CacheConfig(keyGenerator = "keyGenerator", cacheManager = "cacheManager")
@Service
public class ArticleServiceImpl implements IArticleService {

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "UserNewestArticles", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getUserNewestArticles(int userId, int limit) {
        if (userId > 0 && limit > 0) {
            try {
                return articleMapper.getNewestArticles(userId, limit);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "UserAllArticleArchives", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<ArticleCount> getUserAllArticleArchives(int userId) {
        if (userId > 0) {
            try {
                List<ArticleCount> articleCounts = articleMapper.countByUserIdMonthly(userId);
                articleCounts.forEach(ele->{
                    ele.setUserId(userId);
                });
                return articleCounts;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "UserArticlesMonthly", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getUserArticlesMonthly(int userId, String year, String month) {
        if (userId > 0 && Objects.nonNull(year) && Objects.nonNull(month)) {
            try {
                return articleMapper.getByUserIdMonthly(userId, year, month);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<Article> getUserArticlesMonthlyOrderByClickNum(int userId, String year, String month) {
        if (userId > 0 && Objects.nonNull(year) && Objects.nonNull(month)) {
            try {
                return articleMapper.getByUserIdMonthlyOrderByClickNum(userId, year, month);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
