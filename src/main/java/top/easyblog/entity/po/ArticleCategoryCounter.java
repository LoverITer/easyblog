package top.easyblog.entity.po;

import java.io.Serializable;

/**
 * @author HuangXin
 * @since 2020/1/14 14:15
 * 用于映射所有文章分类数量信息
 */
public class ArticleCategoryCounter  implements Serializable {

    private static final long serialVersionUID = -6461128303411237676L;

    private Integer articleNum;
    private String categoryName;

    public Integer getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
