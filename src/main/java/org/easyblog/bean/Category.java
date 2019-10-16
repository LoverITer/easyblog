package org.easyblog.bean;

import java.io.Serializable;

public class Category implements Serializable {

    private static final long serialVersionUID = 8690952682535328046L;
    private Long categoryId;

    private Integer categoryUser;

    private String categoryName;

    private String categoryImageUrl;

    private Integer categoryArticleNum;

    private Integer categoryClickNum;

    private Integer categoryCareNum;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryUser() {
        return categoryUser;
    }

    public void setCategoryUser(Integer categoryUser) {
        this.categoryUser = categoryUser;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl == null ? null : categoryImageUrl.trim();
    }

    public Integer getCategoryArticleNum() {
        return categoryArticleNum;
    }

    public void setCategoryArticleNum(Integer categoryArticleNum) {
        this.categoryArticleNum = categoryArticleNum;
    }

    public Integer getCategoryClickNum() {
        return categoryClickNum;
    }

    public void setCategoryClickNum(Integer categoryClickNum) {
        this.categoryClickNum = categoryClickNum;
    }

    public Integer getCategoryCareNum() {
        return categoryCareNum;
    }

    public void setCategoryCareNum(Integer categoryCareNum) {
        this.categoryCareNum = categoryCareNum;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryUser=" + categoryUser +
                ", categoryName='" + categoryName + '\'' +
                ", categoryImageUrl='" + categoryImageUrl + '\'' +
                ", categoryArticleNum=" + categoryArticleNum +
                ", categoryClickNum=" + categoryClickNum +
                ", categoryCareNum=" + categoryCareNum +
                '}';
    }
}