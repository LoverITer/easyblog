package org.easyblog.bean;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class Category implements Serializable {

    private static final long serialVersionUID = 8690952682535328046L;
    @Id
    private int categoryId;
    private Integer categoryUser;
    private String categoryName;
    private String categoryImageUrl;
    private Integer categoryArticleNum;
    private Integer categoryClickNum;
    private Integer categoryCareNum;
    private Integer display;
    private Date createTime;
    public Category() {
    }

    public Category( Integer categoryUser, String categoryName, String categoryImageUrl, Integer categoryArticleNum, Integer categoryClickNum, Integer categoryCareNum,Integer display) {
        this.categoryUser = categoryUser;
        this.categoryName = categoryName;
        this.categoryImageUrl = categoryImageUrl;
        this.categoryArticleNum = categoryArticleNum;
        this.categoryClickNum = categoryClickNum;
        this.categoryCareNum = categoryCareNum;
        this.display=display;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
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

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
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
                ", display=" + display +
                '}';
    }
}