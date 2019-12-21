package top.easyblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import top.easyblog.bean.Article;
import top.easyblog.bean.ArticleCount;
import top.easyblog.bean.UserComment;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.utils.HtmlParserUtil;
import top.easyblog.commons.utils.MarkdownUtil;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.UserCommentMapper;
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
    private final UserCommentMapper commentMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, UserCommentMapper commentMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
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
                    return parseMarkdown2Text(articleMapper.getUserAllArticles(userId));  //得到用户的所有文章
                } else {
                    return parseMarkdown2Text(articleMapper.getUserArticlesSelective(userId,articleType));  //根据option
                }
            } catch (Exception e) {
                throw new RuntimeException("分页参数异常");
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public PageInfo getUserArticlesPage(int userId, String articleType, PageParam pageParam) {
        PageInfo<Article> pageInfo=null;
        if(userId>0){
            try{
                if(Objects.nonNull(pageParam)){
                    if (ArticleType.Unlimited.getArticleType().equals(articleType)) {
                        //这里特别注意，pageHelper只会对紧跟的第一条语句起作用
                        PageHelper.startPage(pageParam.getPage(),pageParam.getPageSize());
                        List<Article> articles = articleMapper.getUserAllArticles(userId);  //得到用户的所有文章
                        pageInfo=new PageInfo<>(parseMarkdown2Text(articles));
                    } else {
                        PageHelper.startPage(pageParam.getPage(),pageParam.getPageSize());
                        List<Article> articles = articleMapper.getUserArticlesSelective(userId, articleType);//根据option
                        pageInfo=new PageInfo<>(parseMarkdown2Text(articles));
                    }
                }else{
                    throw new RuntimeException("分页参数异常");
                }
            }catch (Exception ex){
               throw new RuntimeException("分页查询异常");
            }
        }
        return pageInfo;
    }

    /**
     * 把Markdown转化为text文本
     * @param articles
     * @return
     */
    private List<Article> parseMarkdown2Text(List<Article> articles){
        if(Objects.nonNull(articles)) {
            articles.forEach(article -> {
                String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent());
                String textContent = HtmlParserUtil.HTML2Text(htmlContent);
                article.setArticleContent(textContent);
            });
        }
        return articles;
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void deleteByPK(int articleId) {
        if(articleId>0){
            try {
                //删除有关这篇文章的所有评论
                List<UserComment> articleComments = commentMapper.getTopCommentsByArticleId(articleId);
                if(articleComments!=null&&articleComments.size()>0){
                    articleComments.forEach(ele->{
                        commentMapper.deleteByPrimaryKey((long)ele.getCommentId());
                    });
                }
                articleMapper.deleteByPrimaryKey((long) articleId);
            }catch (Exception ex){
                ex.printStackTrace();
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
