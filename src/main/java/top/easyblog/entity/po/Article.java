package top.easyblog.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author huangxin
 */
@Data
@AllArgsConstructor
public class Article implements Serializable, Comparable<Article> {

    private static final long serialVersionUID = -1882730239589668817L;

    @Id
    protected Long articleId;
    protected Integer articleUser;
    protected String articleTopic;
    protected Date articlePublishTime;
    protected Integer articleClick;
    protected String articleCategory;
    protected String articleStatus;
    protected String articleTop;
    protected String articleType;
    protected String articleTags;
    protected String articleContent;
    protected Integer articleCommentNum;
    protected String articleAppreciate;
    protected String articleFirstPicture;
    /**
     * 文章分类ID
     **/
    protected Integer categoryId;
    /**
     * 用户头像URL
     **/
    protected String userHeaderImageUrl;
    protected String authorName;


    public Article() {
    }

    public Article(Integer articleUser, String articleTopic, Date articlePublishTime, Integer articleClick, String articleCategory, String articleStatus, String articleTop, String articleType, String articleTags, String articleContent,int articleCommentNum,String articleAppreciate) {
        this.articleAppreciate=articleAppreciate;
        this.articleUser = articleUser;
        this.articleTopic = articleTopic;
        this.articlePublishTime = articlePublishTime;
        this.articleClick = articleClick;
        this.articleCategory = articleCategory;
        this.articleStatus = articleStatus;
        this.articleTop = articleTop;
        this.articleType = articleType;
        this.articleTags = articleTags;
        this.articleContent = articleContent;
        this.articleCommentNum=articleCommentNum;
    }

    public Article(Article article) {
        this(article.getArticleUser(), article.getArticleTopic(),
                article.getArticlePublishTime(), article.articleClick,
                article.getArticleCategory(), article.getArticleStatus(),
                article.getArticleTop(), article.getArticleType(),
                article.getArticleTags(), article.getArticleContent(),
                article.getArticleCommentNum(), article.getArticleAppreciate());
        this.articleId = article.getArticleId();
        this.articleFirstPicture = article.getArticleFirstPicture();
    }


    @Override
    public int compareTo(Article o) {
        if (Objects.nonNull(o)) {
            if (this.articleClick.equals(o.articleClick)) {
                return 0;
            } else if (this.articleClick > o.articleClick) {
                return 1;
            } else {
                return -1;
            }
        } else {

            throw new IllegalArgumentException("Argument can not be null");
        }
    }
}