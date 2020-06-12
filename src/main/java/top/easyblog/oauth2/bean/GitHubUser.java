package top.easyblog.oauth2.bean;

import lombok.Data;

import java.util.Date;

/**
 * GitHub用登录的用户
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/28 22:34
 */
@Data
public class GitHubUser {

    /**用户名*/
    private String login;
    /**GitHub返回的用户Id 或者是和系统绑定后在系统中User表中的Id*/
    private String id;
    /**node_id*/
    private String nodeId;
    /**用户的GitHub头像*/
    private String avatarUrl;
    /**用户创建时间*/
    private Date createTime;
}
