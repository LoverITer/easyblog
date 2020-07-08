package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 *@author huangxin
 */
public class Power implements Serializable {

    private static final long serialVersionUID = 6025381966973267562L;
    @Id
    private Integer powerId;

    private String name;

    public Power() {
    }

    public Power( String name) {
        this.name = name;
    }

    public Integer getPowerId() {
        return powerId;
    }

    public void setPowerId(Integer powerId) {
        this.powerId = powerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }


    @Override
    public String toString() {
        return "Power{" +
                "powerId=" + powerId +
                ", name='" + name + '\'' +
                '}';
    }
}