package top.easyblog.oauth2.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/28 15:51
 */
@Data
public class Oauth {
    private Integer oauthId;
    private String openId;
    private String appType;
    private Integer userId;
    private String status;
    private Date createTime;
}