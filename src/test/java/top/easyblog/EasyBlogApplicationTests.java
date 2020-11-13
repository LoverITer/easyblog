package top.easyblog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.easyblog.entity.dto.Address;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserPower;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.UserMapper;
import top.easyblog.mapper.UserPowerMapper;
import top.easyblog.util.NetWorkUtils;
import top.easyblog.util.SensitiveWordUtils;
import top.easyblog.web.oauth2.impl.GitHubAuthServiceImpl;
import top.easyblog.web.service.impl.UserServiceImpl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EasyBlogApplicationTests {

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserPowerMapper userPowerMapper;

    @Autowired
    GitHubAuthServiceImpl gitHubAuthService;

    @Autowired
    UserServiceImpl userService;



    @Test
    public void testForArticleMapper(){
        //获取全部文章
        List<Article> allArticles = articleMapper.getArticleByCategoryNameFuzzy(new String[]{"java","jvm"},false,-1);

        System.out.println("排序之前：");
        allArticles.forEach(article -> {
            System.out.println(String.format("%-10s %-10s %-20s",article.getArticleId(),article.getArticleClick(),article.getArticleTopic()));
        });

        Objects.requireNonNull(allArticles).sort((o1, o2) -> {
            if (o1 == null || o2 == null) {
                throw new IllegalArgumentException("Argument can not be null");
            }
            if (o1.getArticleClick().equals(o2.getArticleClick())) {
                return 0;
            } else if (o1.getArticleClick() > o2.getArticleClick()) {
                return -1;
            } else {
                return 1;
            }
        });
        System.out.println("排序之后：");
        allArticles.forEach(article -> {
            System.out.println(String.format("%-10s %-10s %-20s",article.getArticleId(),article.getArticleClick(),article.getArticleTopic()));
        });


        //获取排序后的全部文章
        /*List<Article> articles = articleMapper.getArticleByCategoryNameFuzzy(new String[]{"java","jvm"},true,-1);
        articles.forEach(article -> {
            System.out.println(String.format("%-10s %-10s %-20s",article.getArticleId(),article.getArticleClick(),article.getArticleTopic()));
        });*/

        //获取排序后的部分文章
        /*List<Article> articles = articleMapper.getArticleByCategoryNameFuzzy(new String[]{"java","jvm"},true,5);
        articles.forEach(article -> {
            System.out.println(String.format("%-10s %-10s %-20s",article.getArticleId(),article.getArticleClick(),article.getArticleTopic()));
        });*/

    }

    @Test
    public void UserMapperTest(){
        Address address=new Address("中国","西安","临潼");
        User byPrimaryKey = userMapper.getByPrimaryKey(103455L);
        System.out.println(byPrimaryKey.toString());
    }

    @Test
    public void UserPowerTest(){
        String[] power={"Admin","NORM_User","VIP1_User","VIP2_User","VIP3_User","SUPER_User"};

       for(String str:power){
           userPowerMapper.save(new UserPower(str));
       }


    }

    @Test
    public void contextLoads() {
        Article article = new Article();
        articleMapper.saveSelective(article);

    }

    @Test
    public void testProcess() throws IOException {
        final Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String[] cmds = {"C:\\Program Files\\Typora\\Typora.exe","### hello"};
        try {
            process = new ProcessBuilder(cmds).start();
            System.out.println(process.isAlive());            //true
            int exitVal = process.waitFor();
            System.out.println(exitVal);                //0
            System.out.println(process.isAlive());            //false
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }



    @Test
    public void testForOauth(){
        System.out.println(gitHubAuthService.getAccessToken("34"));
        //System.out.println(gitHubAuthService.getAuthorizationUrl());
    }



    @Test
    public void testNetWorkUtils(){
        String location = NetWorkUtils.getLocation("117.136.86.247");
        System.out.println(location);
    }

    @Test
    public void testTemplateEngine(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("title","hello,黄鑫");
    }

    @Test
    public void testSensitiveWordFilter() {
        SensitiveWordUtils sensitiveWordUtils = SensitiveWordUtils.getInstance();
        String searchKey = "去你妹的";
        int i = sensitiveWordUtils.CheckSensitiveWord(searchKey, 0, SensitiveWordUtils.MatchType.MIN_MATCH_TYPE);
        if (i > 0) {
            System.out.printf("这个人输入了非法字符--> %s,不知道他到底要查什么~ %d--> {}", searchKey, 667020);
            System.out.println(sensitiveWordUtils.replaceSensitiveWord(searchKey, "*", SensitiveWordUtils.MatchType.MIN_MATCH_TYPE));
        }
    }


    @Test
    public void sensitiveWordTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = new User();
        user.setUserId(1313);
        user.setUserPassword("你是个二笔");
        user.setUserNickname("我是铁憨憨");
        user.setUserScore(18);
        user.setUserGender("男人婆");
        System.out.println("过滤之前：");

    }

}



