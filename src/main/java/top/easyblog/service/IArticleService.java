package top.easyblog.service;

import com.github.pagehelper.PageInfo;
import top.easyblog.bean.Article;
import top.easyblog.bean.ArticleCount;
import top.easyblog.commons.pagehelper.PageParam;

import java.util.List;

public interface IArticleService {

    /**
     * 保存一篇文章
     *
     * @param article
     * @return
     */
    int saveArticle(Article article);

    Article getArticleById(int articleId);

    List<Article> getUserNewestArticles(int userId, int limit);

    List<ArticleCount> getUserAllArticleArchives(int userId);

    /**
     * @param userId      用户ID
     * @param articleType  查询的文章类型
     * @return   所有文章
     */
    List<Article> getUserArticles(int userId, String articleType);

    /**
     * 分页用户的文章
     * @param userId   用户ID
     * @param articleType   查询的文章类型
     * @param pageParam   分页信息
     * @return  pageInfo
     */
    PageInfo getUserArticlesPage(int userId, String articleType, PageParam pageParam);

    List<Article> getUserArticlesMonthly(int userId, String year, String month);

    List<Article> getUserArticlesMonthlyOrderByClickNum(int userId, String year, String month);

    List<Article> getByCategoryAndUserId(int userId, int categoryId);

    List<Article> getArticlesSelective(Article article, String year, String month);


    List<Article> getArticleByTopic(String query);

    void deleteByUserIdAndTitle(int userId, String title);

    /**
     * 根据主键删除一篇文章
     *
     * @param articleId
     */
    void deleteByPK(int articleId);

    int countUserArticleInCategory(int userId, String categoryName);

    /**
     * 选择性的统计文章的数量
     * @param article
     * @return
     */
    int countSelective(Article article);


    /**
     * 选择性更新文章信息
     * @param article
     */
    int updateSelective(Article article);
}

