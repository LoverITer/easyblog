package top.easyblog.service;

import com.github.pagehelper.PageInfo;
import top.easyblog.bean.Article;
import top.easyblog.bean.ArticleCounter;
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

    Article getArticleById(int articleId,String flag);

    /**
     * 获得某个用户的最新文章
     * @param userId
     * @param limit
     * @return
     */
    List<Article> getUserNewestArticles(int userId, int limit);

    List<ArticleCounter> getUserAllArticleArchives(int userId);

    /**
     * @param userId      用户ID
     * @param articleType  查询的文章类型
     * @return   所有文章
     */
    List<Article> getUserArticles(int userId, String articleType);

    /**
     * 获得所有用户的最新文章
     * @return
     */
    PageInfo<Article> getAllUserNewestArticlesPage(PageParam pageParam);

    /**
     * 获得历史访问量最高的limit篇文章
     * @param limit
     * @return
     */
    List<Article> getAllHistoryFamousArticles(int limit);

    /**
     * 获得猜你喜欢文章
     * @return
     */
    List<Article> getYouMayAlsoLikeArticles();

    /**
     * 获得访问量最大的limit片文章
     * @param limit
     * @return
     */
    List<Article> getMostFamousArticles(int limit);

    /**
     * 分页用户的文章
     * @param userId   用户ID
     * @param articleType   查询的文章类型
     * @param pageParam   分页信息
     * @return  pageInfo
     */
    PageInfo getUserArticlesPage(int userId, String articleType, PageParam pageParam);

    /**
     * 按月份分类文章
     * @param userId
     * @param year
     * @param month
     * @return
     */
    @Deprecated
    List<Article> getUserArticlesMonthly(int userId, String year, String month);

    /**
     * 按月份分类并分页文章
     * @param userId
     * @param year
     * @param month
     * @param pageParam
     * @return
     */
    PageInfo<Article> getUserArticlesMonthlyPage(int userId,String year,String month,PageParam pageParam);

    /**
     * 按月份分类用户的文章并且按访问量排序
     * @param userId
     * @param year
     * @param month
     * @return
     */
    @Deprecated
    List<Article> getUserArticlesMonthlyOrderByClickNum(int userId, String year, String month);

    /**
     * 按月份分类用户的文章并且按访问量排序并且分页
     * @param userId
     * @param year
     * @param month
     * @return
     */
   PageInfo<Article> getUserArticlesMonthlyOrderByClickNumPage(int userId, String year, String month,PageParam pageParam);


    /**
     * 根据用户Id和分类Id获取全部文章
     * @param userId
     * @param categoryId
     * @return
     */
    @Deprecated
    List<Article> getByCategoryAndUserId(int userId, int categoryId);

    /**
     *
     * @param userId
     * @param categoryId
     * @param pageParam  分页参数
     * @return pageInfo
     */
    PageInfo<Article> getByCategoryAndUserIdPage(int userId,int categoryId,PageParam pageParam);

    /**
     * 可选择的查询文章
     * @param article
     * @param year
     * @param month
     * @return
     */
    @Deprecated
    List<Article> getArticlesSelective(Article article, String year, String month);

    /**
     * 可选择的查询文章并且分页
     * @param article
     * @param pageParam 分页参数
     * @return
     */
    PageInfo<Article> getArticlesSelectivePage(Article article, PageParam pageParam);

    List<Article> getArticleByTopic(String query);

    PageInfo<Article> getArticleByTopicPage(String query,PageParam pageParam);

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

    /**
     * 批量更新文章的分类名
     * @param newCategoryName
     * @param oldCategoryName
     * @param userId
     * @return
     */
    int updateArticlesByCategoryName(String newCategoryName,String oldCategoryName,int userId);
}

