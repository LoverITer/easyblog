package top.easyblog.web.oauth2.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.easyblog.config.autoconfig.oauth.GitHubProperties;
import top.easyblog.mapper.OauthMapper;
import top.easyblog.util.NetWorkUtils;
import top.easyblog.web.oauth2.IAuthService;
import top.easyblog.web.oauth2.bean.GitHubUser;
import top.easyblog.web.oauth2.bean.Oauth;
import top.easyblog.web.oauth2.enums.ThirdPartAppType;
import top.easyblog.web.service.IOauthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //发送GET请求获取token的json串
        String responseStr = NetWorkUtils.doGet(url);
        log.info("responseStr:{}", responseStr);
        String accessToken = NetWorkUtils.getMap(responseStr).get("access_token");
        log.info("accessToken:{}", accessToken);
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
                "&state=STATE" +
                "&redirect_uri=" + gitHubProperties.getRedirectUrl();
    }

    @Override
    public GitHubUser getUserInfo(String accessToken) {
        log.info("Request user info from GitHub");
        String userInfoUrl = gitHubProperties.getUserInfoUrl();
        Map<String, String> headers = new HashMap<>(16);
        //json数据
        headers.put("accept", "application/json");
        // AccessToken放在请求头中
        headers.put("Authorization", "token " + accessToken);
        log.info("getUserInfo url:{}", userInfoUrl);
        String userInfo = NetWorkUtils.doGet(userInfoUrl, headers);
        GitHubUser gitHubUser = JSON.parseObject(userInfo, GitHubUser.class);
        assert gitHubUser != null;
        log.info("Get GitHub User info:{}", gitHubUser.toString());
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
    public GitHubUser getUserByOpenIdAndAppType(String openId, ThirdPartAppType appType) {
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
