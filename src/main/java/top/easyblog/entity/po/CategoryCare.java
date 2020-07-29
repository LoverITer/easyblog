package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 *@author huangxin
 */
public class CategoryCare implements Serializable {
    private static final long serialVersionUID = -3709877872290023830L;
    @Id
    private Integer categoryCareId;
    private Integer categoryId;
    private Integer categoryCareUserId;
    private Date careTime;

    public CategoryCare() {
    }

    public CategoryCare( Integer categoryId, Integer categoryCareUserId) {
        this.categoryId = categoryId;
        this.categoryCareUserId = categoryCareUserId;
    }

    public Integer getCategoryCareId() {
        return categoryCareId;
    }

    public void setCategoryCareId(Integer categoryCareId) {
        this.categoryCareId = categoryCareId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryCareUserId() {
        return categoryCareUserId;
    }

    public void setCategoryCareUserId(Integer categoryCareUserId) {
        this.categoryCareUserId = categoryCareUserId;
    }

    public Date getCareTime() {
        return careTime;
    }

    public void setCareTime(Date careTime) {
        this.careTime = careTime;
    }

    @Override
    public String toString() {
        return "CategoryCare{" +
                "categoryCareId=" + categoryCareId +
                ", categoryId=" + categoryId +
                ", categoryCareUserId=" + categoryCareUserId +
                ", careTime=" + careTime +
                '}';
    }
}
