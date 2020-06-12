package top.easyblog.oauth2.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import top.easyblog.config.autoconfig.oauth.GitHubProperties;
import top.easyblog.mapper.OauthMapper;
import top.easyblog.oauth2.IAuthService;
import top.easyblog.oauth2.bean.GitHubUser;
import top.easyblog.oauth2.bean.Oauth;
import top.easyblog.oauth2.enums.AppType;
import top.easyblog.service.IOauthService;

import java.util.List;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/28 15:50
 */
@Service
@Slf4j
public class GitHubAuthServiceImpl implements IAuthService<GitHubUser>, IOauthService<GitHubUser> {


    @Autowired
    private GitHubProperties gitHubProperties;

    @Autowired
    private OauthMapper oauthMapper;

    /**
     * 根据GitHub返回的code获得token
     *
     * @param code code
     * @return Token
     */
    @Override
    public String getAccessToken(String code) {
        String url = gitHubProperties.getAccessTokenUrl() +
                "?client_id=" + gitHubProperties.getClientId() +
                "&client_secret=" + gitHubProperties.getClientSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        log.info("getAccessToke url : {}", url);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("accept", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        //响应内容
        String responseStr = response.getBody();
        log.info("responseStr={}", responseStr);

        //JSON解析响应的内容
        JSONObject object = JSON.parseObject(responseStr);
        String accessToken = object.getString("access_token");
        log.info("accessToken={}", accessToken);
        return accessToken;
    }

    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }

    @Override
    public String getAuthorizationUrl() {
        return gitHubProperties.getAuthorizeUrl() +
                "?client_id=" + gitHubProperties.getClientId() +
                "&redirect_uri=" + gitHubProperties.getRedirectUrl();
    }

    @Override
    public GitHubUser getUserInfo(String accessToken) {
        String userInfoUrl = gitHubProperties.getUserInfoUrl();
        log.info("getUserInfo url:{}", userInfoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        // AccessToken放在请求头中
        headers.add("Authorization", "token " + accessToken);
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        // get请求方式
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, String.class);
        String userInfo = response.getBody();
        GitHubUser gitHubUser = JSON.parseObject(userInfo, GitHubUser.class);
        assert gitHubUser != null;
        log.info("gitHubUser info={}", gitHubUser.toString());
        return gitHubUser;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int register(GitHubUser gitHubUser,int userId) {
        if (gitHubUser != null) {
            try {
                Oauth oauth = new Oauth();
                oauth.setAppType("GitHub");
                oauth.setOpenId(gitHubUser.getId());
                oauth.setUserId(userId);
                return oauthMapper.save(oauth);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return -1;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public GitHubUser getUserByOpenIdAndAppType(String openId, AppType appType) {
        try {
            if (!StringUtils.isEmpty(openId) && !StringUtils.isEmpty(appType)) {
                Oauth oauth = new Oauth();
                oauth.setOpenId(openId);
                oauth.setAppType(appType.getAppType());
                List<Oauth> user = oauthMapper.getUserSelective(oauth);
                if (user != null) {
                    Oauth u = user.get(0);
                    GitHubUser gitHubUser = new GitHubUser();
                    gitHubUser.setId(u.getOpenId());
                    gitHubUser.setCreateTime(u.getCreateTime());
                    gitHubUser.setId(String.valueOf(u.getUserId()));
                    return gitHubUser;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
