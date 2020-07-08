package top.easyblog.entity.po;

import java.io.Serializable;


/**
 * 用于映射文章表的统计信息
 *
 * @author huangxin
 */
public class ArticleCounter implements Serializable {


    private static final long serialVersionUID = 7441077545294577920L;
    private int userId;
    private String date;
    private int count;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
