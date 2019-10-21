package org.easyblog.bean;

import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable {

    private static final long serialVersionUID = -1882730239589668817L;

    private Long articleId;

    private Integer articleUser;

    private String articleTopic;

    private Date articlePublishTime;

    private Integer articleClick;

    private String articleCategory;

    private String articleStatus;

    private Boolean articleTop;

    private String articleType;

    private String articleTags;

    private String articleContent;

    public Article() {
    }

    public Article(Integer articleUser, String articleTopic, Date articlePublishTime, Integer articleClick, String articleCategory, String articleStatus, Boolean articleTop, String articleType, String articleTags, String articleContent) {
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
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Integer getArticleUser() {
        return articleUser;
    }

    public void setArticleUser(Integer articleUser) {
        this.articleUser = articleUser;
    }

    public String getArticleTopic() {
        return articleTopic;
    }

    public void setArticleTopic(String articleTopic) {
        this.articleTopic = articleTopic == null ? null : articleTopic.trim();
    }

    public Date getArticlePublishTime() {
        return articlePublishTime;
    }

    public void setArticlePublishTime(Date articlePublishTime) {
        this.articlePublishTime = articlePublishTime;
    }

    public Integer getArticleClick() {
        return articleClick;
    }

    public void setArticleClick(Integer articleClick) {
        this.articleClick = articleClick;
    }

    public String getArticleCategory() {
        return articleCategory;
    }

    public void setArticleCategory(String articleCategory) {
        this.articleCategory = articleCategory == null ? null : articleCategory.trim();
    }

    public String getArticleStatus() {
        return articleStatus;
    }

    public void setArticleStatus(String articleStatus) {
        this.articleStatus = articleStatus == null ? null : articleStatus.trim();
    }

    public Boolean getArticleTop() {
        return articleTop;
    }

    public void setArticleTop(Boolean articleTop) {
        this.articleTop = articleTop;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType == null ? null : articleType.trim();
    }

    public String getArticleTags() {
        return articleTags;
    }

    public void setArticleTags(String articleTags) {
        this.articleTags = articleTags == null ? null : articleTags.trim();
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent == null ? null : articleContent.trim();
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleId=" + articleId +
                ", articleUser=" + articleUser +
                ", articleTopic='" + articleTopic + '\'' +
                ", articlePublishTime=" + articlePublishTime +
                ", articleClick=" + articleClick +
                ", articleCategory='" + articleCategory + '\'' +
                ", articleStatus='" + articleStatus + '\'' +
                ", articleTop=" + articleTop +
                ", articleType='" + articleType + '\'' +
                ", articleTags='" + articleTags + '\'' +
                ", articleContent='" + articleContent + '\'' +
                '}';
    }
}