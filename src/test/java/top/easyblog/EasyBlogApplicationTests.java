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
import top.easyblog.mapper.ArticleMapper;
import top.easyblog.mapper.UserMapper;
import top.easyblog.mapper.UserPowerMapper;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EasyBlogApplicationTests {

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserPowerMapper userPowerMapper;

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

}
