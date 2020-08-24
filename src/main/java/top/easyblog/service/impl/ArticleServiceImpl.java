package top.easyblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.common.enums.ArticleType;
import top.easyblog.common.exception.IllegalPageParameterException;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.entity.po.*;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.CategoryMapper;
import top.easyblog.mapper.UserCommentMapper;
import top.easyblog.mapper.UserMapper;
import top.easyblog.markdown.TextForm;
import top.easyblog.service.IArticleService;
import top.easyblog.util.HtmlParserUtils;
import top.easyblog.util.MarkdownUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
@Service
public class ArticleServiceImpl implements IArticleService {

    private final ArticleMapper articleMapper;
    private final UserCommentMapper commentMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, UserCommentMapper commentMapper, CategoryMapper categoryMapper, UserMapper userMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int saveArticle(Article article) {
        try {
            if (Objects.nonNull(article)) {
                return articleMapper.save(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getAllArticles() {
        return articleMapper.getAll();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getAllNoneFirstPicArticles() {
        List<Article> articles = null;
        try {
            articles = articleMapper.getAllNoneFirstPicArticles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public Article getArticleById(int articleId, TextForm textForm) {
        Article article = null;
        if (articleId > 0) {
            try {
                article = articleMapper.getByPrimaryKey((long) articleId);
                if (TextForm.HTML.equalsIgnoreCase(textForm)) {
                    parseMarkdown2HTML(article);
                } else if (TextForm.TXT.equalsIgnoreCase(textForm)) {
                    parseMarkdown2Text(article);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return article;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getUserNewestArticles(int userId, int limit) {
        if (userId > 0 && limit > 0) {
            try {
                return articleMapper.getNewestArticles(userId, limit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getYouMayAlsoLikeArticles() {
        List<Article> articles = new ArrayList<>();
        try {
            List<ArticleCategoryCounter> categories = articleMapper.getArticleCategoryCounter(10);
            categories.forEach(category -> {
                //根据排名靠前的10个分类查询出来对应的2篇文章
                List<Article> lists = articleMapper.getByCategoryWithLimit("%" + category.getCategoryName() + "%", 2);
                articles.addAll(lists);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getAllHistoryFamousArticles(int limit) {
        List<Article> articles = null;
        if (limit > 0) {
            try {
                articles = articleMapper.getAllHistoryFamousArticles(limit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return articles;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public PageInfo<Article> getAllUserNewestArticlesPage(PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if (Objects.nonNull(pageParam)) {
            try {
                PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                //查最近一个月内的所有数据
                List<Article> articles = articleMapper.getAllUserNewestArticles();
                if (Objects.isNull(articles) || articles.size() < 10) {
                    //查历史最新的20条数据
                    articles = articleMapper.getAllUserHistoryNewestArticles(20);
                }
                if (articles != null) {
                    articles.forEach(article -> {
                        try {
                            Integer userId = article.getArticleUser();
                            User user = userMapper.getByPrimaryKey((long) userId);
                            if (user != null) {
                                article.setUserHeaderImageUrl(user.getUserHeaderImgUrl());
                            }
                            Category category = categoryMapper.getCategoryByUserIdAndName(userId, article.getArticleCategory());
                            if (category != null) {
                                article.setCategoryId(category.getCategoryId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    //把MarkDown文本转换为普通文本
                    parseMarkdowns2Text(articles);
                    pageInfo = new PageInfo<>(articles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalPageParameterException();
        }
        return pageInfo;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getMostFamousArticles(int limit) {
        List<Article> articles = null;
        try {
            if (limit > 0) {
                articles = articleMapper.getAllMostFamousArticles(limit);
                parseMarkdowns2Text(articles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<ArticleCounter> getUserAllArticleArchives(int userId) {
        if (userId > 0) {
            try {
                List<ArticleCounter> articleCounts = articleMapper.countByUserIdMonthly(userId);
                articleCounts.forEach(ele -> {
                    ele.setUserId(userId);
                });
                return articleCounts;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getUserArticles(int userId, String articleType) {
        if (userId > 0) {
            try {
                if (ArticleType.Unlimited.getArticleType().equals(articleType)) {
                    //得到用户的所有文章
                    return parseMarkdowns2Text(articleMapper.getUserAllArticles(userId));
                } else {
                    //根据option
                    return parseMarkdowns2Text(articleMapper.getUserArticlesSelective(userId, articleType));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public PageInfo getUserArticlesPage(int userId, String articleType, PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if (userId > 0) {
            try {
                if (Objects.nonNull(pageParam)) {
                    if (ArticleType.Unlimited.getArticleType().equals(articleType)) {
                        //这里特别注意，pageHelper只会对紧跟的第一条语句起作用
                        PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                        //得到用户的所有文章
                        List<Article> articles = articleMapper.getUserAllArticles(userId);
                        pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                    } else {
                        PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                        //根据option
                        List<Article> articles = articleMapper.getUserArticlesSelective(userId, articleType);
                        pageInfo = new PageInfo<>(parseMarkdowns2Text(articles));
                    }
                } else {
                    throw new IllegalPageParameterException();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("分页查询异常");
            }
        }
        return pageInfo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public List<Article> getUserArticlesMonthly(int userId, String year, String month) {
        if (userId > 0 && StringUtil.isNotEmpty(year) && StringUtil.isNotEmpty(month)) {
            try {
                return parseMarkdowns2Text(articleMapper.getByUserIdMonthly(userId, year, month));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

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
    public PageInfo<Article> getUserAllPage(PageParam pageParam) {
        PageInfo<Article> pageInfo = null;
        if(pageParam!=null){
            try{
                PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
                List<Article> articles = articleMapper.getAll();
                pageInfo=new PageInfo<>(parseMarkdowns2Text(articles));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return pageInfo;
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

    @Override
    public List<Article> getArticlesSelective(Article article, String year, String month) {
        try {
            if (article != null) {
                return articleMapper.getArticlesSelective(article, year, month);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PageInfo<Article> getArticlesSelectivePage(Article article, PageParam pageParam) {
        return getArticlesSelectivePage(article, null, null, pageParam);
    }


    public PageInfo<Article> getArticlesSelectivePage(Article article, String year, String month, PageParam pageParam) {
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

    @Override
    public List<Article> getArticleByTopic(String query) {
        if (StringUtil.isNotEmpty(query)) {
            try {
                return parseMarkdowns2Text(articleMapper.getUsersArticleByQueryString(query + "%"));
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
                    List<Article> articles = articleMapper.getUsersArticleByQueryString("%"+query + "%");
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class, readOnly = true)
    @Override
    public List<Article> getArticleByCategoryFuzzy(String[] keys,Boolean order,int limit) {
        if (Objects.nonNull(keys) && keys.length > 0) {
            List<Article> articles = articleMapper.getArticleByCategoryNameFuzzy(keys,order,limit);
            parseMarkdowns2Text(articles);
            if(articles!=null){
                articles.stream().parallel().forEach(article -> {
                    Category category = categoryMapper.getCategoryByUserIdAndName(article.getArticleUser(), article.getArticleCategory());
                    article.setCategoryId(Objects.requireNonNull(category).getCategoryId());
                });
            }
            return articles;
        } else {
            throw new IllegalArgumentException("illegal args for 'keys':" + Arrays.toString(keys));
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Override
    public Article getArticleByPK(Long articleId) {
        if(articleId>0){
            return articleMapper.getByPrimaryKey(articleId);
        }
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int updateArticlesByCategoryName(String newCategoryName, String oldCategoryName, int userId) {
        if (Objects.nonNull(newCategoryName) && Objects.nonNull(oldCategoryName) && userId > 0) {
            try {
                return articleMapper.updateArticlesByUserIdAndArticleCategory(newCategoryName, oldCategoryName, userId);
            } catch (Exception e) {
                e.printStackTrace();
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
     *
     * @param article
     * @return
     */
    private Article parseMarkdown2Text(Article article) {
        if (Objects.nonNull(article)) {
            String htmlContent = parseMarkdown2HTML(article).getArticleContent();
            String textContent = HtmlParserUtils.HTML2Text(htmlContent);
            article.setArticleContent(textContent);
        }
        return article;
    }

    /**
     * 把Markdown文章内容转化为HTML内容
     *
     * @param article
     * @return
     */
    private Article parseMarkdown2HTML(Article article) {
        if (Objects.nonNull(article)) {
            article.setArticleContent(MarkdownUtils.markdownToHtmlExtensions(article.getArticleContent()));
        }
        return article;
    }

}
