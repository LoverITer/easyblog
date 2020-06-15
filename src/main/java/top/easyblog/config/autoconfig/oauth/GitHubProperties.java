package top.easyblog.config.autoconfig.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/28 11:18
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth.github",ignoreUnknownFields = true)
public class GitHubProperties {
    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String redirectUrl;
    private String accessTokenUrl;
    private String userInfoUrl;
}
