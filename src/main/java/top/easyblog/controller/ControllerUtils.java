package top.easyblog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import top.easyblog.bean.Article;
import top.easyblog.bean.ArticleCounter;
import top.easyblog.bean.Category;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CategoryServiceImpl;
import top.easyblog.service.impl.CommentServiceImpl;
import top.easyblog.service.impl.UserAttentionImpl;

import java.util.List;
import java.util.Objects;


class ControllerUtils {

    private static Logger log = LoggerFactory.getLogger(ControllerUtils.class);
    private final CategoryServiceImpl categoryService;
    private final ArticleServiceImpl articleService;
    private final CommentServiceImpl commentService;
    private final UserAttentionImpl userAttention;
    volatile private static ControllerUtils controllerUtils;

    private ControllerUtils(CategoryServiceImpl categoryService, ArticleServiceImpl articleService, CommentServiceImpl commentService, UserAttentionImpl userAttention) {
        this.categoryService = categoryService;
        this.articleService = articleService;
        this.commentService = commentService;
        this.userAttention = userAttention;
    }

    public static ControllerUtils getInstance(CategoryServiceImpl categoryService, ArticleServiceImpl articleService, CommentServiceImpl commentService, UserAttentionImpl userAttention){
        if(Objects.isNull(controllerUtils)){
            synchronized (ControllerUtils.class){
                if (Objects.isNull(controllerUtils)) {
                    controllerUtils=new ControllerUtils(categoryService,articleService,commentService,userAttention);
                }
            }
        }
        return controllerUtils;
    }

    /***
     * 这是几个页面公共需要查询数据
     * @param model
     * @param userId
     * @param articleType
     */
    void getArticleUserInfo(Model model, int userId, String articleType) {
        try {
            //作者的所有允许显示的分类
            List<Category> lists = categoryService.getUserAllViableCategory(userId);
            //作者的所有归档
            List<ArticleCounter> archives = articleService.getUserAllArticleArchives(userId);
            //作者的最新文章5篇文章
            List<Article> newestArticles = articleService.getUserNewestArticles(userId, 5);
            //作者的原创文章数
            Article article = new Article();
            article.setArticleUser(userId);
            article.setArticleType(ArticleType.Original.getArticleType());
            int originalArticle = articleService.countSelective(article);
            //作者指定类型的所有文章
            List<Article> articles = articleService.getUserArticles(userId, articleType);
            //关于我的文章的评论数
            int receiveCommentNum = commentService.getReceiveCommentNum(userId);
            //我的关注数
            int attentionNumOfMe = userAttention.countAttentionNumOfMe(userId);
            model.addAttribute("attentionNumOfMe",attentionNumOfMe);
            model.addAttribute("receiveCommentNum",receiveCommentNum);
            model.addAttribute("categories", lists);
            model.addAttribute("archives", archives);
            model.addAttribute("newestArticles", newestArticles);
            model.addAttribute("articles", articles);
            model.addAttribute("articleNum", articles.size());
            model.addAttribute("originalArticleNum", originalArticle);
        }catch (Exception  e){
            log.error(e.getMessage());
        }
    }


}
