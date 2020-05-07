#基础镜像tomcat
FROM tomcat:latest

#维护者信息
MAINTAINER huangxin981230@163.com

#定义 变量
ENV Tomcat_Home /usr/local/tomcat
ENV WEBAPP $Tomcat_Home/webapps

#删除容器内webapps下的所有东西
RUN rm -rf $WEBAPP/*

#把本地war包添加进来作为根应用
COPY ./target/easyblog-beat.war $WEBAPP/ROOT.war
#配置工作目录
WORKDIR $Tomcat_Home/webapps

# 容器暴露8080端口
EXPOSE 8080

#启动容器
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]
