package org.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface ArticleMapper extends BaseMapper<Article> {

    int saveSelective(Article record);

    int updateByPrimaryKeySelective(Article record);

    int updateByPrimaryKeyWithContent(Article record);

    /**
     * 查询用户的所有文章数
     * @param userId
     * @return
     */
    int countByUserId(@Param("userId") int userId);


    /**
     * 得到用户的所有原创文章，按时间降序排列
     * @param userId
     * @return
     */
    List<Article> getUserAllArticles(@Param("userId") int userId);


    List<Article> getUserAllOrgArticles(@Param("userId") int userId);


    /**
     * 查询最近的limit 篇文章
     * @param userId
     * @param limit
     * @return
     */
    List<Article> getNewestArticles(@Param("userId") int userId,@Param("limit") int limit);

    /**
     * 得到用户limit篇访问量最高的文章
     * @param userId
     * @param limit
     * @return
     */
    List<Article> getHotArticles(@Param("userId") int userId,@Param("limit") int limit);

    /**
     * 按照月份统计用户的userId这个月的文章数
     * @param userId
     * @return
     */
    List<ArticleCount> countByUserIdMonthly(@Param("userId") int userId);

    /**
     * 按月份查询用户的文章,按照时间降序排列
     * @param userId
     * @param year
     * @param month
     * @return
     */
    List<Article> getByUserIdMonthly(@Param("userId") int userId,@Param("year") String year,@Param("month") String month);


    /**
     * 按月份查询用户的文章,按照访问次数降序排列
     * @param userId
     * @param year
     * @param month
     * @return
     */
    List<Article> getByUserIdMonthlyOrderByClickNum(@Param("userId") int userId,@Param("year") String year,@Param("month") String month);

    /**
     * 得到用户某个分类下的所有文章，按时间降序排列
     * @param userId
     * @param categoryId
     * @return
     */
    List<Article> getByCategoryAndUserId(@Param("userId") int userId,@Param("categoryId") int categoryId);


    /**
     * 删除用户的草稿文件
     * @param userId
     * @param title
     */
    void deleteArticleByUserIdAndTitle(@Param("userId") int userId,@Param("title") String title);
}