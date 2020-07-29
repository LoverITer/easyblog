package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;


/**
 * @author huangxin
 */
public class UserPower implements Serializable {

    private static final long serialVersionUID = -3934031817257705616L;
    @Id
    private Integer powerId;

    private String powerName;

    public UserPower() {
    }

    public UserPower(String powerName) {
        this.powerName = powerName;
    }

    public Integer getPowerId() {
        return powerId;
    }

    public void setPowerId(Integer powerId) {
        this.powerId = powerId;
    }

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName == null ? null : powerName.trim();
    }

    @Override
    public String toString() {
        return "UserPower{" +
                "powerId=" + powerId +
                ", powerName='" + powerName + '\'' +
                '}';
    }
}