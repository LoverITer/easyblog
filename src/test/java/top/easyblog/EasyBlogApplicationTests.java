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
       // User user=new User("李狗狗","123456","李刚","F",new Date(),"3456788","15709888888","32322@qq.com",address.toString(),0,10000,"/local","",new Date(),"192.168.0.0","145.6.7.7","2019-8-7 12:20",0,0,2);
        //userMapper.save(user);
       /* List<User> users=new ArrayList<>();
        for(int j=0;j<100;j++) {
            for (int i = 0; i < 1000; i++) {
                User user = new User("李狗狗"+i, "123456" + i, "李刚", "F", new Date(), "3456788" + i, "15709888888", i + "32322@qq.com", address.toString(), 0, 10000, "/local", "", "192.168.0.0", "145.6.7.7", "2019-8-7 12:20", 0, 0, 2);
                users.add(user);
                user=null;  //let GC work
            }

            userMapper.saveBatch(users);
        }*/

        //List<User> usrs=userMapper.getAll();
       // usrs.forEach(ele-> System.out.println(ele));
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

}
