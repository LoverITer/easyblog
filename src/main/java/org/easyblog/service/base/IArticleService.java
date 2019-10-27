package org.easyblog.service.base;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;

import java.util.List;

public interface IArticleService {

    List<Article> getUserNewestArticles(int userId,int limit);


    List<ArticleCount>  getUserAllArticleArchives(int userId);


    List<Article> getUserArticlesMonthly(int userId,String year,String month);


    List<Article> getUserArticlesMonthlyOrderByClickNum(int userId,String year,String month);
}
