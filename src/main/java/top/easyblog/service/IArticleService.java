package top.easyblog.service;

import com.github.pagehelper.PageInfo;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.ArticleCounter;
import top.easyblog.markdown.TextForm;

import java.util.List;

/**
 * @author huanxin
 */
public interface IArticleService {

    /**
     * 保存一篇文章
     *
     * @param article Article对象
     * @return int
     */
    int saveArticle(Article article);


    /**
     * 获取所有的文章
     * @return
     */
    @Deprecated
    List<Article> getAllArticles();

    /**
     * 获得所有文章并分页
     *
     * @param pageParam 分页参数
     * @return
     */
    PageInfo<Article> getUserAllPage(PageParam pageParam);


    /**
     * 根据文章Id获取文章
     *
     * @param articleId 文章Id
     * @param textForm      要获得的文本类型，支持html和text
     * @return Article
     */
    Article getArticleById(int articleId, TextForm textForm);

    /**
     * 查詢所有沒有首圖的文章
     *
     * @return List
     */
    List<Article> getAllNoneFirstPicArticles();

    /**
     * 获得某个用户的最新文章
     *
     * @param userId 用户Id
     * @param limit  文章篇数
     * @return List
     */
    List<Article> getUserNewestArticles(int userId, int limit);

    /**
     * 获得某个用户的苏即有文章归档
     *
     * @param userId 用户ID
     * @return List
     */
    List<ArticleCounter> getUserAllArticleArchives(int userId);

    /**
     * 获得用户的所有文章
     *
     * @param userId      用户ID
     * @param articleType 查询的文章类型
     * @return 所有文章
     */
    List<Article> getUserArticles(int userId, String articleType);

    /**
     * 获得所有用户的最新文章
     *
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Article> getAllUserNewestArticlesPage(PageParam pageParam);

    /**
     * 获得历史访问量最高的limit篇文章
     *
     * @param limit 文章数量
     * @return List<Article>
     */
    List<Article> getAllHistoryFamousArticles(int limit);

    /**
     * 获得猜你喜欢文章
     *
     * @return List<Article>
     */
    List<Article> getYouMayAlsoLikeArticles();

    /**
     * 获得访问量最大的limit片文章
     *
     * @param limit 文章数量
     * @return List<Article>
     */
    List<Article> getMostFamousArticles(int limit);

    /**
     * 分页用户的文章
     *
     * @param userId      用户ID
     * @param articleType 查询的文章类型
     * @param pageParam   分页信息
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo getUserArticlesPage(int userId, String articleType, PageParam pageParam);

    /**
     * 按月份分类文章
     *
     * @param userId 用户Id
     * @param year   年份
     * @param month  月份
     * @return List<Article>
     */
    @Deprecated
    List<Article> getUserArticlesMonthly(int userId, String year, String month);

    /**
     * 按月份分类并分页文章
     *
     * @param userId    用户Id
     * @param year      年
     * @param month     月
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Article> getUserArticlesMonthlyPage(int userId, String year, String month, PageParam pageParam);

    /**
     * 按月份分类用户的文章并且按访问量排序
     *
     * @param userId 用户Id
     * @param year   年
     * @param month  月
     * @return List<Article>
     */
    @Deprecated
    List<Article> getUserArticlesMonthlyOrderByClickNum(int userId, String year, String month);



    /**
     * 按月份分类用户的文章并且按访问量排序并且分页
     *
     * @param userId 用户Id
     * @param year   年
     * @param month  月
     * @param pageParam  分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Article> getUserArticlesMonthlyOrderByClickNumPage(int userId, String year, String month, PageParam pageParam);


    /**
     * 根据用户Id和分类Id获取全部文章
     *
     * @param userId     用户Id
     * @param categoryId 分类Id
     * @return List<Article>
     */
    @Deprecated
    List<Article> getByCategoryAndUserId(int userId, int categoryId);

    /**
     * 根据用户Id和分类Id获取全部文章并分页
     *
     * @param userId     用户Id
     * @param categoryId 分类Id
     * @param pageParam  分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Article> getByCategoryAndUserIdPage(int userId, int categoryId, PageParam pageParam);

    /**
     * 可选择的查询文章
     *
     * @param article Article对象
     * @param year    年
     * @param month   月
     * @return
     */
    @Deprecated
    List<Article> getArticlesSelective(Article article, String year, String month);

    /**
     * 可选择的查询文章并且分页
     *
     * @param article   Article
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Article> getArticlesSelectivePage(Article article, PageParam pageParam);

    /**
     * 根据文章的标题查询文章
     *
     * @param query 文章标题查询参数
     * @return List<Article>
     */
    @Deprecated
    List<Article> getArticleByTopic(String query);


    /**
     * 根据给定的key数组，模糊查询文章
     * @param keys 分类名
     * @param order  是否需要排序,指定true为需要排序，false为不需要排序
     * @param limit  指定一个大于0的数，用于获取Top n个查询结果,如果指定为负数表示获取全部结果
     * @return java.util.List
     */
    List<Article> getArticleByCategoryFuzzy(String[] keys, Boolean order, int limit);


    /**
     * 根据文章的标题查询文章并分页
     *
     * @param query     文章标题查询参数
     * @param pageParam 分页参数
     * @return com.github.pagehelper.PageInfo
     */
    PageInfo<Article> getArticleByTopicPage(String query, PageParam pageParam);

    /**
     * 根据用户Id和文章标题删除文章
     *
     * @param userId 用户Id
     * @param title  文章标题
     */
    void deleteByUserIdAndTitle(int userId, String title);

    /**
     * 根据主键删除一篇文章
     *
     * @param articleId 文章Id
     */
    void deleteByPK(int articleId);

    /**
     * 统计出用户在某个分类下对的文章数量
     *
     * @param userId       用户Id
     * @param categoryName 分类名
     * @return int
     */
    int countUserArticleInCategory(int userId, String categoryName);

    /**
     * 选择性的统计文章的数量
     *
     * @param article Article对象
     * @return int
     */
    int countSelective(Article article);


    /**
     * 选择性更新文章信息
     *
     * @param article Article对象
     * @return int
     */
    int updateSelective(Article article);

    /**
     * 批量更新文章的分类名
     *
     * @param newCategoryName 新分类名
     * @param oldCategoryName 原分类名
     * @param userId          用户Id
     * @return int
     */
    int updateArticlesByCategoryName(String newCategoryName, String oldCategoryName, int userId);

    Article getArticleByPK(Long articleId);
}

