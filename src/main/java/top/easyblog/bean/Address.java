package top.easyblog.bean;

import java.io.Serializable;

/**
 * 用户的地址信息
 * @author huangxin
 */
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Override
    public String toString() {
        return country +"," + city + "," + county ;
    }
}
