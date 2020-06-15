package top.easyblog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.easyblog.bean.Address;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.bean.UserPower;
import top.easyblog.common.util.NetWorkUtils;
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.UserMapper;
import top.easyblog.mapper.UserPowerMapper;
import top.easyblog.oauth2.impl.GitHubAuthServiceImpl;

import java.io.IOException;
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


}



