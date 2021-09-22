EasyBlog博客网站
======== 
![](https://img.shields.io/github/tag/pandao/editor.md.svg) 
![](https://img.shields.io/github/release/pandao/editor.md.svg) 
![](https://img.shields.io/bower/v/editor.md.svg)
![](https://camo.githubusercontent.com/b28cf4816a133c75598a4d5ba5e182b9be15b439/68747470733a2f2f696d672e736869656c64732e696f2f686578706d2f6c2f706c75672e7376673f7374796c653d666c61742d737175617265)
![](https://camo.githubusercontent.com/b74f0c55895a3e31913261c2933ce1233f69fefb/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f626c6f672d2545352538442539412545352541452541322d6f72616e67652e7376673f7374796c653d666c61742d737175617265)
 

📚 1、项目简介
------------
EasyBlog是一个小而美的个人技术博客系统。系统基于SpringBoot+MyBatis+Redis搭建，网站线上地址：https://www.easyblog.top/

如果该项目对您有帮助，您可以点一下右上角的"star" 支持一下 谢谢!  

或者可以"follow"一下,该项目会一直持续更新,不断添加新功能和优化 
 
 
2、组织结构 
------------
```text
easyblog
├── config   --通用配置
├── entity   --实体类，对应数据库表
├── global   --系统全局的一些公共类，比如枚举类、Markdown转换功能、邮件发送功能等
├── log      --日志
├── mapper   --数据持久层，和数据库打交道的一层封装
├── util     --工具类
└── web      --web服务
    ├── controller   --web服务控制层
    ├── exception    --全局的异常处理
    ├── oauth2       --第三方认证服务  
    └── service      --Web服务业务层 
```
 
![](http://image.easyblog.top/1602231059275ecb9e1d1-34ab-4f15-90a1-f4a57cea397f.png) 
 


3、效果图
------------
#### 网址：https://www.easyblog.top

##### 3.1 博客首页
![](http://image.easyblog.top/160223007724674093b32-5da4-42f1-98b5-812b83643bc1.png)

##### 3.2 登录/注册页面
![](http://image.easyblog.top/1595993819335b42ba4e2-e57d-4db6-a3ba-f00bfa3827de.png)

##### 3.3 文章页面
![](http://image.easyblog.top/1602232499677215d69bb-319a-4cb6-9070-3664af5107cd.png)

##### 3.4 文章分类页面
![](http://image.easyblog.top/158142018903330c50b98-a5e8-444b-92ec-789e1b6a489d.png)

##### 3.5 文章归档页面
![](http://image.easyblog.top/15814203243294ed6e732-edcb-4604-b7fc-5d5d7a73bb2c.png)

##### 3.6 全部文章列表页面
![](http://image.easyblog.top/1581420469583b059dab2-15a4-44a7-a96f-cdece5b88f8e.png)


##### 3.7 文章编辑页面
![](http://image.easyblog.top/15814208027537f962c90-9fbd-4e0a-8a0e-ea4b31dc0291.png)

##### 3.8 文章管理页面
![](http://image.easyblog.top/1581420877327788dfd73-0e33-4625-ad00-295b4b2f2cfe.png)



4、所用技术或工具
----------

* [Semantic UI](https://onebugman.cn/semantic-ui/elements/button.php#-floated)：前端页面主体框架
* [SpringBoot 2.1.8](https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/)：后端主体框架
* [MySQL](https://www.mysql.com/)：数据存储
* [Druid](https://github.com/alibaba/druid)：数据库连接池
* [MyBatis](https://mybatis.org/mybatis-3/zh/index.html)：ORM框架
* [MybatisGenerator]()：mybatis代码自动生成插件
* [Redis](https://redis.io/)：缓存服务、分布式session
* [Thymeleaf](https://www.thymeleaf.org/)：编写动态页面
* [Mybatis-PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)：分页插件
* [Editor.md](https://pandao.github.io/editor.md/)：在线Markdown编辑器
* [PrismJS](https://github.com/PrismJS/prism)：一个轻量，健壮，优雅的语法高亮插件库
* [Typo](https://github.com/sofish/typo.css)：中文网页重设与排版插件
* [CommonMark](https://github.com/atlassian/commonmark-java) ：Markdown解析器
* [Docker](https://docker.com):容器化部署

5、EasyBlog的构建与运行
------

##### 5.1 环境搭建

* 安装JDK 8或者更高的版本,程序中用到了java 8中的函数式编程的一些东西
* 安装MySQL,SQL文件在项目的根目录下,可以直接导入MySQL服务器执行
* 安装Maven(3.6版本以上),安装Redis
* 修改配置文件。application-dev.yml和application-pro.yml中的数据库配置需要变成自己的配置。前者是开发环境，后者是生产环境下的配置，想要那个环境起作用就在application的spring.profiles.active指定（dev或pro）

##### 5.2 拉取代码并构建应用
从这里拉取代码到你本地，使用`IntelliJ IDEA`打开项目，可以直接使用Maven打成war包，然后部署到Tomcat的webapps目录下（最好将war包的名字改为ROOT.war），这样就完成了部署

也可以使用Docker容器化部署：[详情点击这里](https://www.easyblog.top/article/details/211)


6、联系方式
------
QQ: 2489868503

Email: huangxin981230@163.com


版权
-------
该项目遵循 Apache 2.0 license.

