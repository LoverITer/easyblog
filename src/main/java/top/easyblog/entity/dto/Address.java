package top.easyblog.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户的地址信息
 * @author huangxin
 */
@Data
public class Address implements Serializable {

    private static final long serialVersionUID = -2744598988959076601L;
    private String country;
    private String city;
    private String county;

    public Address() {
    }

    public Address(String country, String city, String county) {
        this.country = country;
        this.city = city;
        this.county = county;
    }

    @Override
    public String toString() {
        return country +"," + city + "," + county ;
    }

}
