use easyblog;

create table user
(
    user_id               int          not null AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    user_nickname         varchar(32)  not null comment '用户昵称',
    user_password         varchar(256) not null comment '用户密码',
    user_name             varchar(32)    default '' comment '用户真实姓名',
    user_gender           enum ('F','M') default 'M' comment '用户性别',
    user_birthday         date comment '用户生日',
    user_qq               varchar(11)    default '' comment '用户qq',
    user_phone            varchar(12)    default '' comment '用户手机',
    user_mail             varchar(256)   default '' comment '用户email',
    user_address          varchar(256)   default '' comment '用户地址',
    user_score            int            default 0 comment '用户积分',
    user_rank             int            default 0 comment '用户排名',
    user_headerImg_url    varchar(1024)  default '' comment '用户头像',
    user_description      varchar(300)   default '' comment '用户自我描述',
    user_register_time    datetime     not null comment '用户注册时间',
    user_register_ip      varchar(100) not null comment '用户注册时的地址',
    user_last_login_ip    varchar(100) not null comment '用户上次登录的ip',
    user_last_update_time datetime     not null comment '用户上次更改信息时间',
    user_lock             boolean      not null comment '是否锁定账户',
    user_freeze           boolean      not null comment '是否冻结账户',
    user_power            int          not null comment '用户权限',
    foreign key (user_power) references user_power (power_id)
) ENGINE = INNODB
  default charset = UTF8MB4;


create table user_power
(
    power_id   int not null auto_increment comment '用户权限id',
    power_name varchar(256) comment '用户权限',
    primary key (power_id)
) ENGINE = INNODB
  default charset = UTF8MB4;


create table power
(
    power_id int         not null auto_increment comment '权限ID',
    name     varchar(30) not null comment '权限名',
    primary key (power_id)
) ENGINE = INNODB
  default charset = UTF8MB4;

/**
  系统向用户发送邮件的记录
 */
create table user_mail_log
(
    log_id   bigint   not null auto_increment comment '日志记录主键',
    user_id  int      not null comment '用户id',
    context  text default '' comment '日志内容',
    log_time datetime not null comment '记录日志时间',
    primary key (log_id),
    foreign key (user_id) references user (user_id)
) ENGINE = INNODB
  default charset = UTF8MB4;

/**
  系统向用户发送短信的记录
 */
create table user_phone_log
(
    log_id   int      not null auto_increment comment '日志记录主键',
    user_id  int      not null comment '用户id',
    context  text comment '日志内容',
    log_time datetime not null comment '记录日志时间',
    primary key (log_id),
    foreign key (user_id) references user (user_id)
) ENGINE = INNODB
  default charset = UTF8MB4;

/**
  用户登录记录日志
 */
create table user_signIn_log
(
    log_id         bigint       not null auto_increment comment '日志记录主键',
    user_id        int          not null comment '用户id',
    login_ip       varchar(20)  not null comment '用户登录的ip',
    login_location varchar(100) not null comment '根据ip计算出来的用户登录的实际地址',
    login_time     datetime     not null comment '登录时间',
    primary key (log_id),
    foreign key (user_id) references user (user_id)
) ENGINE = INNODB
  default charset = UTF8MB4;

/**
  用户私信记录表
 */
create table secret_message
(
    send_id         int      not null comment '发送者id',
    receive_id      int      not null comment '接收者id',
    message_time    datetime not null comment '发送私信时间',
    message_topic   varchar(20) comment '私信标题',
    message_content text comment '私信内容',
    primary key (send_id, receive_id, message_time),
    foreign key (send_id) references user (user_id),
    foreign key (receive_id) references user (user_id)
) ENGINE = INNODB
  default charset = UTF8MB4;

/**
  文章表
 */
create table article
(
    article_id           bigint                 not null primary key auto_increment comment '文章自增id',
    article_user         int                    not null comment '文章所属用户',
    article_topic        varchar(30)            not null comment '文章标题',
    article_publish_time datetime               not null comment '文章创建时间',
    article_content      longtext comment '文章内容',
    article_click        int                    not null default 0 comment '文章查看次数',
    article_category     varchar(256)                    default '' comment '文章分类',
    article_status       enum ('0','1','2','3') not null default '2' comment '文章的状态,0公开  1私有 2保存为草稿 3垃圾桟中的文章',
    article_top          boolean                         default false comment '文章是否置顶，默认不置顶',
    article_type         enum ('0','1','2')     not null default '0' comment '文章的类型,0原创 1转载 2翻译',
    article_tags         varchar(256)           not null default '' comment '文章标签',
    foreign key (article_user) references user (user_id)
) ENGINE = INNODB
  default charset = UTF8MB4;

/**
  文章分类
 */
create table category
(
    category_id          bigint       not null primary key auto_increment comment '分类自增ID',
    category_user        int          not null comment '分类所属用户',
    category_name        varchar(256) not null comment '分类的名字',
    category_image_url   varchar(256) not null default '' comment '分类的图片',
    category_article_num int          not null default 0 comment '用户该分类的文章数目',
    category_click_num   int          not null default 0 comment '用户该专栏的访问量',
    category_care_num    int          not null default 0 comment '用户该专栏的关注量',
    foreign key (category_user) references user (user_id)
) ENGINE = INNODB
  default charset = UTF8MB4;


create table user_comment
(
    comment_id       int      not null primary key auto_increment comment '用户评论自增ID',
    comment_send     int      not null comment '接收评论的用户',
    comment_received int      not null comment '评论的用户',
    article_id       bigint   not null comment '评论所属的文章',
    comment_time     datetime not null comment '评论时间',
    comment_content  text     not null comment '评论的内容',
    like_num         int default 0 comment '评论获赞数',
    pid              int default 0 comment '父级评论ID ，0表示这是个父级(root)评论',
    level            int default 1 comment '评论所处的深度',
    foreign key (comment_send) references user (user_id),
    foreign key (comment_received) references user (user_id),
    foreign key (article_id) references article (article_id)
) ENGINE = INNODB
  default charset = UTF8MB4;


create table user_attention
(
    id             smallint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    user_id        int      NOT NULL COMMENT '用户ID',
    attention_id   int      NOT NULL COMMENT '关注的ID',
    attention_time datetime not null comment '关注时间',
    foreign key (user_id) references user (user_id),
    foreign key (attention_id) references user (user_id),
    PRIMARY KEY (id)
) ENGINE = INNODB
  default charset = UTF8MB4;






