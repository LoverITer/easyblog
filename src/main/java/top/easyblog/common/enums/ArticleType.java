package top.easyblog.common.enums;

/**
 * 文章的类型
 *
 * @author huangxin
 */
public enum ArticleType {
    /**
     * 原创文章
     */
    Original("0"),
    /**
     * 转载文章
     */
    Reprint("1"),
    /**
     * 翻译文章
     */
    Translate("2"),
    /**
     * 不限
     */
    Unlimited("3");

    String articleType;

    ArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }
}
