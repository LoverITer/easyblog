package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.ArticleCategoryCounter;
import top.easyblog.entity.po.ArticleCounter;
import top.easyblog.mapper.core.BaseMapper;

import java.util.List;

/**
 * @author Huangxin
 */
@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * @param record
     * @return
     */
    int saveSelective(Article record);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Article record);

    /**
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithContent(Article record);

    /**
     * @param newArticleCategory
     * @param oldArticleCategory
     * @param userId
     * @return
     */
    int updateArticlesByUserIdAndArticleCategory(@Param(value = "newArticleCategory") String newArticleCategory, @Param(value = "oldArticleCategory") String oldArticleCategory, @Param(value = "userId") int userId);

    /**
     * 查询用户的所有文章数
     *
     * @param userId
     * @return
     */
    int countByUserId(@Param("userId") int userId);


    /**
     * 选择性的统计文章数
     *
     * @param article
     * @return
     */
    int countSelective(@Param("article") Article article);


    /**
     * 查询用户某一分类下的文章数量
     *
     * @param userId
     * @param categoryName
     * @return
     */
    int countUserArticlesInCategory(@Param("userId") int userId, @Param("categoryName") String categoryName);


    /**
     * 根据不同的条件动态的查询用户的文章
     *
     * @param article 查询的其他条件
     * @param year    查询的年份   如果没有写null
     * @param month   查询的月份  如果没有写null
     * @return
     */
    List<Article> getArticlesSelective(@Param("article") Article article, @Param("year") String year, @Param("month") String month);

    /**
     * 得到用户的所有原创文章，按时间降序排列
     *
     * @param userId
     * @return
     */
    List<Article> getUserAllArticles(@Param("userId") int userId);

    /**
     * 筛选用户不同的文章数据
     *
     * @param userId
     * @return
     */
    List<Article> getUserArticlesSelective(@Param("userId") int userId, @Param("articleType") String articleType);


    /**
     * 查询最近的limit 篇文章
     *
     * @param userId
     * @param limit
     * @return
     */
    List<Article> getNewestArticles(@Param("userId") int userId, @Param("limit") int limit);


    /**
     * 获得所有没有首图的文章
     *
     * @return
     */
    List<Article> getAllNoneFirstPicArticles();


    /**
     * 获得所有用户最近文章
     *
     * @return
     */
    List<Article> getAllUserNewestArticles();

    /**
     * 获得所有用户的历史最新的limit篇文章
     *
     * @param limit
     * @return
     */
    List<Article> getAllUserHistoryNewestArticles(@Param("limit") int limit);

    /**
     * 统计分类下文章数量
     *
     * @param limit
     * @return
     */
    List<ArticleCategoryCounter> getArticleCategoryCounter(@Param("limit") int limit);

    /**
     * 获得指定数量的访问量最高的某个分类的文章
     *
     * @param key   分类的关键字
     * @param limit 数量
     * @return
     */
    List<Article> getByCategoryWithLimit(String key, int limit);

    /**
     * 获得所有文章中访问量最高的limit片文章
     *
     * @param limit
     * @return
     */
    List<Article> getAllMostFamousArticles(@Param("limit") int limit);


    /**
     * 获得历史访问量最高的limit篇文章
     *
     * @param limit
     * @return
     */
    List<Article> getAllHistoryFamousArticles(@Param("limit") int limit);

    /**
     * 得到用户limit篇访问量最高的文章
     *
     * @param userId
     * @param limit
     * @return
     */
    List<Article> getHotArticles(@Param("userId") int userId, @Param("limit") int limit);

    /**
     * 按照月份统计用户的userId这个月的文章数
     *
     * @param userId
     * @return
     */
    List<ArticleCounter> countByUserIdMonthly(@Param("userId") int userId);

    /**
     * 按月份查询用户的文章,按照时间降序排列
     *
     * @param userId
     * @param year
     * @param month
     * @return
     */
    List<Article> getByUserIdMonthly(@Param("userId") int userId, @Param("year") String year, @Param("month") String month);


    /**
     * 按月份查询用户的文章,按照访问次数降序排列
     *
     * @param userId
     * @param year
     * @param month
     * @return
     */
    List<Article> getByUserIdMonthlyOrderByClickNum(@Param("userId") int userId, @Param("year") String year, @Param("month") String month);

    /**
     * 得到用户某个分类下的所有文章，按时间降序排列
     *
     * @param userId
     * @param categoryId
     * @return
     */
    List<Article> getByCategoryAndUserId(@Param("userId") int userId, @Param("categoryId") int categoryId);

    /**
     * 根据文章名模糊查询
     * @param query  模糊查询的key
     * @return  java.util.List
     */
    List<Article> getUsersArticleByQueryString(@Param("query") String query);


    /**
     * 根据分类名模糊查询这一类的文章
     *
     * @param query 分类名
     * @param order  是否需要排序,指定true为需要排序，false为不需要排序
     * @param limit  指定一个大于0的数，用于获取Top n个查询结果,如果指定为负数表示获取全部结果
     * @return java.util.List
     */
    List<Article> getArticleByCategoryNameFuzzy(@Param("query") String[] query, @Param("order") Boolean order, @Param("limit") int limit);


    /**
     * 删除用户的草稿文件
     *
     * @param userId
     * @param title
     */
    void deleteArticleByUserIdAndTitle(@Param("userId") int userId, @Param("title") String title);
}