package top.easyblog.service.impl;

import top.easyblog.bean.Article;
import top.easyblog.bean.ArticleCount;
import top.easyblog.commons.ArticleType;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.service.IArticleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

    @Transactional(isolation =Isolation.REPEATABLE_READ)
    @CachePut(cacheNames = "article",condition = "#result>0")
    @Override
    public int saveArticle(Article article) {
        try {
            if(Objects.nonNull(article)) {
                return articleMapper.save(article);
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "article", condition = "#result!=null")
    @Override
    public Article getArticleById(int articleId) {
        if(articleId>0){
            try{
                return articleMapper.getByPrimaryKey((long) articleId);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "userArticles", condition = "#result!=null&&#result.size()>0")
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
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<ArticleCount> getUserAllArticleArchives(int userId) {
        if (userId > 0) {
            try {
                List<ArticleCount> articleCounts = articleMapper.countByUserIdMonthly(userId);
                articleCounts.forEach(ele -> {
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

    @Transactional(isolation =Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles",condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getUserArticles(int userId, String articleType) {
        if (userId > 0) {
            try {
                if (ArticleType.Unlimited.getArticleType().equals(articleType)) {
                    return articleMapper.getUserAllArticles(userId);  //得到用户的所有文章
                } else {
                    return articleMapper.getUserArticlesSelective(userId,articleType);  //根据option
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&#result.size()>0")
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&result.size()>0")
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


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&result.size()>0")
    @Override
    public List<Article> getByCategoryAndUserId(int userId, int categoryId) {
        if (userId > 0 && categoryId > 0) {
            try {
                return articleMapper.getByCategoryAndUserId(userId, categoryId);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles",condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getArticlesSelective(Article article, String year, String month) {
       try {
           if (article != null) {
               return  articleMapper.getArticlesSelective(article, year, month);
           }
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles",condition = "#result!=null&&result.size()>0")
    @Override
    public List<Article> getArticleByTopic(String query) {
        if(null!=query){
            try{
                return articleMapper.getUsersArticleByQueryString("%"+query+"%");
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(cacheNames = "article")
    @Override
    public void deleteByUserIdAndTitle(int userId, String title) {
        if(userId>0&&!"".equals(title)){
            try {
                articleMapper.deleteArticleByUserIdAndTitle(userId, title);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteByPK(int articleId) {
        if(articleId>0){
            try {
                articleMapper.deleteByPrimaryKey((long) articleId);
            }catch (Exception ignored){
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int countUserArticleInCategory(int userId, String categoryName) {
        if(userId>0&&Objects.nonNull(categoryName)){
            try {
               return articleMapper.countUserArticlesInCategory(userId, categoryName);
            }catch (Exception e){
                return 0;
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int countSelective(Article article) {
        if(Objects.nonNull(article)){
            try{
                return articleMapper.countSelective(article);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override
    public int updateSelective(Article article) {
        if(Objects.nonNull(article)){
            try{
                return  articleMapper.updateByPrimaryKeySelective(article);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }
}
