SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `article_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '文章自增id',
  `article_user` int(0) NOT NULL COMMENT '文章所属用户',
  `article_topic` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `article_publish_time` datetime(0) NOT NULL COMMENT '文章创建时间',
  `article_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
  `article_click` int(0) NOT NULL DEFAULT 0 COMMENT '文章查看次数',
  `article_category` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '文章分类',
  `article_status` enum('0','1','2','3') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '文章的状态,0公开  1私有 2保存为草稿 3垃圾箱中的文章',
  `article_top` enum('0','1') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '文章是否置顶，默认不置顶',
  `article_type` enum('0','1','2') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '文章的类型,0原创 1转载 2翻译',
  `article_tags` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文章标签',
  `article_comment_num` int(0) NOT NULL DEFAULT 0 COMMENT '文章评论个数',
  `article_appreciate` enum('0','1') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '文章是否开启赞赏功能：0 不开启  1开启',
  `article_first_picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章首图，默认为null',
  PRIMARY KEY (`article_id`) USING BTREE,
  INDEX `idx_user`(`article_user`) USING BTREE,
  INDEX `idx_id_toppic_category_type`(`article_id`, `article_topic`, `article_category`, `article_type`) USING BTREE COMMENT '复合索引',
  INDEX `idx_catrgory`(`article_category`) USING BTREE COMMENT '文章分类索引',
  INDEX `idx_id_click`(`article_id`, `article_click`) USING BTREE,
  INDEX `idx_article_click`(`article_click`) USING BTREE,
  INDEX `idx_article-status-publish-time`(`article_status`, `article_publish_time`) USING BTREE,
  INDEX `idx_publish_time`(`article_publish_time`) USING BTREE,
  INDEX `idx_img`(`article_first_picture`) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`article_user`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 269 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `category_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '分类自增ID',
  `category_user` int(0) NOT NULL COMMENT '分类所属用户',
  `category_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类的名字',
  `category_image_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '分类的图片',
  `category_article_num` int(0) NOT NULL DEFAULT 0 COMMENT '用户该分类的文章数目',
  `category_click_num` int(0) NOT NULL DEFAULT 0 COMMENT '用户该专栏的访问量',
  `category_care_num` int(0) NOT NULL DEFAULT 0 COMMENT '用户该专栏的关注量',
  `display` enum('0','1','2') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '分类的显示状态 0 不显示 1 显示 2 放到垃圾桶中',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `category_description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专栏的描述',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_category-user_category-name`(`category_user`, `category_name`) USING BTREE,
  CONSTRAINT `category_ibfk_1` FOREIGN KEY (`category_user`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category_care
-- ----------------------------
DROP TABLE IF EXISTS `category_care`;
CREATE TABLE `category_care`  (
  `category_care_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(0) NOT NULL,
  `category_care_user_id` int(0) NOT NULL,
  `care_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`category_care_id`) USING BTREE,
  INDEX `category_id`(`category_id`) USING BTREE,
  CONSTRAINT `category_care_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oauth
-- ----------------------------
DROP TABLE IF EXISTS `oauth`;
CREATE TABLE `oauth`  (
  `oauth_id` int(0) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `app_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` int(0) NULL DEFAULT NULL,
  `status` enum('1','2','3') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '账户的状态：1,2,3  1 表示正常 2 表示暂时禁用 3 表示永久禁用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`oauth_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for power
-- ----------------------------
DROP TABLE IF EXISTS `power`;
CREATE TABLE `power`  (
  `power_id` tinyint(0) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名',
  PRIMARY KEY (`power_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for secret_message
-- ----------------------------
DROP TABLE IF EXISTS `secret_message`;
CREATE TABLE `secret_message`  (
  `send_id` int(0) NOT NULL COMMENT '发送者id',
  `receive_id` int(0) NOT NULL COMMENT '接收者id',
  `message_time` datetime(0) NOT NULL COMMENT '发送私信时间',
  `message_topic` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '私信标题',
  `message_context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '私信内容',
  PRIMARY KEY (`send_id`, `receive_id`, `message_time`) USING BTREE,
  INDEX `receive_id`(`receive_id`) USING BTREE,
  CONSTRAINT `secret_message_ibfk_1` FOREIGN KEY (`send_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `secret_message_ibfk_2` FOREIGN KEY (`receive_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户真实姓名',
  `user_gender` enum('F','M') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'M' COMMENT '用户性别',
  `user_birthday` date NULL DEFAULT NULL COMMENT '用户生日',
  `user_phone` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户手机',
  `user_mail` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户email',
  `user_address` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户地址',
  `user_score` int(0) NULL DEFAULT 0 COMMENT '用户积分',
  `user_rank` int(0) NULL DEFAULT 0 COMMENT '用户排名',
  `user_headerImg_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户头像',
  `user_description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户自我描述',
  `user_register_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '用户注册时间',
  `user_register_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户注册时的ip和物理地址',
  `user_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '用户上次更改信息时间',
  `user_lock` int(0) NOT NULL COMMENT '是否锁定账户 1是锁定  0是不锁定  锁定后无法再解锁',
  `user_freeze` int(0) NOT NULL COMMENT '是否冻结账户 1是冻结 0是不冻结  冻结后可以解冻',
  `user_power` tinyint(0) NOT NULL COMMENT '用户权限,1是管理员 2是VIP用户，3是普通用户',
  `user_level` int(4) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '用户等级',
  `user_visit` int(12) UNSIGNED ZEROFILL NULL DEFAULT 000000000000 COMMENT '用户访问量',
  `user_job_position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的职位',
  `user_prefession` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的行业',
  `user_hobby` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户的爱好',
  `user_tech` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户的技术栈',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `user_power`(`user_power`) USING BTREE,
  INDEX `idx_pk_phone_pwd`(`user_id`, `user_password`, `user_phone`) USING BTREE,
  INDEX `idx_pk_mail_pwd`(`user_id`, `user_password`, `user_mail`) USING BTREE,
  INDEX `idx_pk_nickname`(`user_id`, `user_nickname`) USING BTREE,
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`user_power`) REFERENCES `user_power` (`power_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 667024 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account`  (
  `account_id` int(0) NOT NULL AUTO_INCREMENT,
  `account_user` int(0) NOT NULL,
  `github` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `qq` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `wechat` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `steam` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `twitter` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `weibo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`account_id`) USING BTREE,
  INDEX `account_user`(`account_user`) USING BTREE,
  CONSTRAINT `user_account_ibfk_1` FOREIGN KEY (`account_user`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_attention
-- ----------------------------
DROP TABLE IF EXISTS `user_attention`;
CREATE TABLE `user_attention`  (
  `id` smallint(0) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` int(0) NOT NULL COMMENT '被关注用户ID',
  `attention_id` int(0) NOT NULL COMMENT '关注者的ID',
  `attention_time` datetime(0) NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `attention_id`(`attention_id`) USING BTREE,
  CONSTRAINT `user_attention_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_attention_ibfk_2` FOREIGN KEY (`attention_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_comment
-- ----------------------------
DROP TABLE IF EXISTS `user_comment`;
CREATE TABLE `user_comment`  (
  `comment_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户评论自增ID',
  `comment_send` int(0) NOT NULL COMMENT '接收评论的用户',
  `comment_received` int(0) NOT NULL COMMENT '评论的用户',
  `article_id` bigint(0) NOT NULL COMMENT '评论所属的文章',
  `comment_time` datetime(0) NOT NULL COMMENT '评论时间',
  `comment_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论的内容',
  `like_num` int(0) NULL DEFAULT 0 COMMENT '评论获赞数',
  `pid` int(0) NULL DEFAULT NULL COMMENT '父级评论ID ，0表示这是个父级(root)评论',
  `level` int(0) NULL DEFAULT 1 COMMENT '评论所处的深度',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `comment_send`(`comment_send`) USING BTREE,
  INDEX `comment_received`(`comment_received`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  CONSTRAINT `user_comment_ibfk_1` FOREIGN KEY (`comment_send`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_comment_ibfk_2` FOREIGN KEY (`comment_received`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_comment_ibfk_3` FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_mail_log
-- ----------------------------
DROP TABLE IF EXISTS `user_mail_log`;
CREATE TABLE `user_mail_log`  (
  `log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志记录主键',
  `email` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日志内容',
  `log_time` datetime(0) NOT NULL COMMENT '记录日志时间',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 116 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_phone_log
-- ----------------------------
DROP TABLE IF EXISTS `user_phone_log`;
CREATE TABLE `user_phone_log`  (
  `log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志记录主键',
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日志内容',
  `log_time` datetime(0) NOT NULL COMMENT '记录日志时间',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_power
-- ----------------------------
DROP TABLE IF EXISTS `user_power`;
CREATE TABLE `user_power`  (
  `power_id` tinyint(0) NOT NULL AUTO_INCREMENT COMMENT '用户权限id',
  `power_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户权限',
  PRIMARY KEY (`power_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_signIn_log
-- ----------------------------
DROP TABLE IF EXISTS `user_signIn_log`;
CREATE TABLE `user_signIn_log`  (
  `log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志记录主键',
  `user_id` int(0) NOT NULL COMMENT '用户id',
  `login_ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户登录的ip',
  `login_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '根据ip计算出来的用户登录的实际地址',
  `login_time` datetime(0) NOT NULL COMMENT '登录时间',
  `login_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录结果',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `user_signIn_log_ibfk_1`(`user_id`) USING BTREE,
  CONSTRAINT `user_signIn_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 717 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
