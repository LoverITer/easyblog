package org.easyblog.service.base;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;

import java.util.List;

public interface IArticleService {

    /**
     * 保存一篇文章
     * @param article
     * @return
     */
    int saveArticle(Article article);

    Article getArticleById(int articleId);

    List<Article> getUserNewestArticles(int userId,int limit);

    List<ArticleCount>  getUserAllArticleArchives(int userId);

    /**
     *
     * @param userId  用户ID
     * @param articleType
     * @return
     */
    List<Article> getUserArticles(int userId,String articleType);

    List<Article> getUserArticlesMonthly(int userId,String year,String month);

    List<Article> getUserArticlesMonthlyOrderByClickNum(int userId,String year,String month);

    List<Article> getByCategoryAndUserId(int userId,int categoryId);

    List<Article> getArticlesSelective(Article article,String year,String month);

    void deleteByUserIdAndTitle(int userId,String title);

    /**
     * 根据主键删除一篇文章
     * @param articleId
     */
    void deleteByPK(int articleId);

    int countUserArticleInCategory(int userId,String categoryName);

}
