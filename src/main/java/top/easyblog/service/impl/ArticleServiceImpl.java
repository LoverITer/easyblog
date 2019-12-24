package top.easyblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.bean.Article;
import top.easyblog.bean.ArticleCount;
import top.easyblog.bean.UserComment;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.utils.HtmlParserUtil;
import top.easyblog.commons.utils.MarkdownUtil;
import top.easyblog.handler.exception.IllegalPageParameterException;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.UserCommentMapper;
import top.easyblog.service.IArticleService;

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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CachePut(cacheNames = "article", condition = "#result>0")
    @Override
    public int saveArticle(Article article) {
        try {
            if (Objects.nonNull(article)) {
                return articleMapper.save(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "article", condition = "#result!=null")
    @Override
    public Article getArticleById(int articleId) {
        if (articleId > 0) {
            try {
                return parseMarkdown2Text(articleMapper.getByPrimaryKey((long) articleId));
            } catch (Exception e) {
                e.printStackTrace();
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getUserArticles(int userId, String articleType) {
        if (userId > 0) {
            try {
                if (ArticleType.Unlimited.getArticleType().equals(articleType)) {
                    return parseMarkdowns2Text(articleMapper.getUserAllArticles(userId));  //得到用户的所有文章
                } else {
                    return parseMarkdowns2Text(articleMapper.getUserArticlesSelective(userId, articleType));  //根据option
                }
            } catch (Exception e) {
                throw new RuntimeException("发生未知异常");
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public PageInfo getUserArticlesPage(int userId, String articleType, PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if (userId > 0) {
            try {
                if (Objects.nonNull(pageParam)) {
                    if (ArticleType.Unlimited.getArticleType().equals(articleType)) {
                        //这里特别注意，pageHelper只会对紧跟的第一条语句起作用
                        PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                        List<Article> articles = articleMapper.getUserAllArticles(userId);  //得到用户的所有文章
                        pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                    } else {
                        PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                        List<Article> articles = articleMapper.getUserArticlesSelective(userId, articleType);//根据option
                        pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                    }
                } else {
                    throw new IllegalPageParameterException();
                }
            } catch (Exception ex) {
                throw new RuntimeException("分页查询异常");
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getUserArticlesMonthly(int userId, String year, String month) {
        if (userId > 0 && StringUtil.isNotEmpty(year) && StringUtil.isNotEmpty(month)) {
            try {
                return parseMarkdowns2Text(articleMapper.getByUserIdMonthly(userId, year, month));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public PageInfo<Article> getUserArticlesMonthlyPage(int userId, String year, String month, PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if (userId > 0 && StringUtil.isNotEmpty(year) && StringUtil.isNotEmpty(month)) {
            if (Objects.nonNull(pageParam)) {
                try {
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Article> articles = articleMapper.getByUserIdMonthly(userId, year, month);
                    pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                } catch (Exception e) {
                    //
                }
            } else {
                throw new IllegalPageParameterException();
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&result.size()>0")
    @Override
    public List<Article> getUserArticlesMonthlyOrderByClickNum(int userId, String year, String month) {
        if (userId > 0 && StringUtil.isNotEmpty(year) && StringUtil.isNotEmpty(month)) {
            try {
                return parseMarkdowns2Text(articleMapper.getByUserIdMonthlyOrderByClickNum(userId, year, month));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public PageInfo<Article> getUserArticlesMonthlyOrderByClickNumPage(int userId, String year, String month, PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if (userId > 0 && StringUtil.isNotEmpty(year) && StringUtil.isNotEmpty(month)) {
            try {
                if (Objects.nonNull(pageParam)) {
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Article> articles = articleMapper.getByUserIdMonthlyOrderByClickNum(userId, year, month);
                    pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                } else {
                    throw new IllegalPageParameterException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&result.size()>0")
    @Override
    public List<Article> getByCategoryAndUserId(int userId, int categoryId) {
        if (userId > 0 && categoryId > 0) {
            try {
                return articleMapper.getByCategoryAndUserId(userId, categoryId);
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return null;
    }

    @Override
    public PageInfo<Article> getByCategoryAndUserIdPage(int userId, int categoryId, PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        try {
            if (userId > 0 && categoryId > 0) {
                if (Objects.nonNull(pageParam)) {
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Article> articlesInCategory = articleMapper.getByCategoryAndUserId(userId, categoryId);
                    pageInfo = new PageInfo<>(parseMarkdowns2Text(articlesInCategory));
                } else {
                    throw new IllegalPageParameterException();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }

        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<Article> getArticlesSelective(Article article, String year, String month) {
        try {
            if (article != null) {
                return articleMapper.getArticlesSelective(article, year, month);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public PageInfo<Article> getArticlesSelectivePage(Article article, PageParam pageParam) {
       return getArticlesSelectivePage(article,null, null,pageParam);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PageInfo<Article> getArticlesSelectivePage(Article article, String year, String month,PageParam pageParam){
        PageInfo<Article> pageInfo = null;
        if (Objects.nonNull(article)) {
            if (Objects.nonNull(pageParam)) {
                try {
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Article> articles = articleMapper.getArticlesSelective(article, year, month);
                    pageInfo = new PageInfo<>(articles);
                } catch (Exception e) {
                    throw new RuntimeException(e.getCause());
                }
            } else {
                throw new IllegalPageParameterException();
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "articles", condition = "#result!=null&&result.size()>0")
    @Override
    public List<Article> getArticleByTopic(String query) {
        if (StringUtil.isNotEmpty(query)) {
            try {
                return parseMarkdowns2Text(articleMapper.getUsersArticleByQueryString("%" + query + "%"));
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return null;
    }

    @Override
    public PageInfo<Article> getArticleByTopicPage(String query, PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if (StringUtil.isNotEmpty(query)) {
            if (Objects.nonNull(pageParam)) {
                try {
                    PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                    List<Article> articles = articleMapper.getUsersArticleByQueryString("%" + query + "%");
                    pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                } catch (Exception e) {
                    throw new RuntimeException(e.getCause());
                }
            } else {
                throw new IllegalPageParameterException();
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(cacheNames = "article")
    @Override
    public void deleteByUserIdAndTitle(int userId, String title) {
        if (userId > 0 && StringUtil.isNotEmpty(title)) {
            try {
                articleMapper.deleteArticleByUserIdAndTitle(userId, title);
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void deleteByPK(int articleId) {
        if (articleId > 0) {
            try {
                //删除有关这篇文章的所有评论
                List<UserComment> articleComments = commentMapper.getTopCommentsByArticleId(articleId);
                if (articleComments != null && articleComments.size() > 0) {
                    articleComments.forEach(ele -> {
                        commentMapper.deleteByPrimaryKey((long) ele.getCommentId());
                    });
                }
                articleMapper.deleteByPrimaryKey((long) articleId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int countUserArticleInCategory(int userId, String categoryName) {
        if (userId > 0 && StringUtil.isNotEmpty(categoryName)) {
            try {
                return articleMapper.countUserArticlesInCategory(userId, categoryName);
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int countSelective(Article article) {
        if (Objects.nonNull(article)) {
            try {
                return articleMapper.countSelective(article);
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int updateSelective(Article article) {
        if (Objects.nonNull(article)) {
            try {
                return articleMapper.updateByPrimaryKeySelective(article);
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return 0;
    }


    /**
     * 把批量的Markdown转化为text文本
     *
     * @param articles
     * @return
     */
    private List<Article> parseMarkdowns2Text(List<Article> articles) {
        if (Objects.nonNull(articles)) {
            articles.forEach(this::parseMarkdown2Text);
        }
        return articles;
    }

    /**
     * 把单个Markdown转化为文本
     * @param article
     * @return
     */
    private Article parseMarkdown2Text(Article article) {
        if (Objects.nonNull(article)) {
            String htmlContent = parseMarkdown2HTML(article).getArticleContent();
            String textContent = HtmlParserUtil.HTML2Text(htmlContent);
            article.setArticleContent(textContent);
        }
        return article;
    }

    /**
     * 把Markdo文章内容转化为HTML内容
     *
     * @param article
     * @return
     */
    private Article parseMarkdown2HTML(Article article) {
        if (Objects.nonNull(article)) {
            article.setArticleContent(MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent()));
        }
        return article;
    }

}
