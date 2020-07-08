package top.easyblog.entity.vo;

import lombok.Data;
import top.easyblog.entity.po.Article;

import java.io.Serializable;

/**
 * 文章视图展示对象
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/07/08 10:11
 */
@Data
public class ArticleVO extends Article implements Serializable {

    private static final long serialVersionUID = 609673058689456024L;
    /**
     * 文章分类ID
     **/
    private Integer categoryId;
    /**
     * 用户头像URL
     **/
    private String userHeaderImageUrl;
    private String authorName;

    public ArticleVO(Article article, Integer categoryId, String userHeaderImageUrl, String authorName) {
        super(article);
        this.categoryId = categoryId;
        this.userHeaderImageUrl = userHeaderImageUrl;
        this.authorName = authorName;
    }

}
