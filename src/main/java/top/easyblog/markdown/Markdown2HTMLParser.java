package top.easyblog.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;

/**
 * @author HuangXin
 * @since 2020/1/24 12:00
 * Markdown 渲染为HTML的具体实现
 */
public class Markdown2HTMLParser extends AbstractMarkdownParser {
    @Override
    public String render2Html(String markdown) {
       /* MutableDataSet options = new MutableDataSet()
                .set(
                        Parser.EXTENSIONS,
                        Arrays.asList(TablesExtension.create(),
                                JekyllTagExtension.create(),
                                TocExtension.create(),
                                SimTocExtension.create())
                ).set(
                        TocExtension.LEVELS, 255).set(
                        TocExtension.TITLE, "Table of Contents").set(
                        TocExtension.DIV_CLASS, "toc");*/
        DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
                Extensions.ALL
        );


        Parser parser = Parser.builder(OPTIONS).build();
        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS)
                .build();

        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        System.out.println(html);
        return html;
    }

    public static void main(String[] args) {
        String mark = "### Netty入门—实现实现简单的HTTP服务器\n" +
                "======\n" +
                "Netty是一个基于事件驱动的异步非阻塞网络应用程序框架，可以用于快速开发可维护的高性能协议服务器和客户端，今天我们就用Netty来简单实现一个Http服务器。注意：如果要实现一个完整的ttp服务器，那将是十分复杂的。所以，这里只是实现最基本的，请求-响应。\n" +
                "> 要求：\n" +
                "> 1、Netty 服务器监听8080端口，当浏览器发出http://localhost:8080请求后，服务器给浏览器返回一句话“服务器收到请求！Hello Client”;\n" +
                "> 2、服务器要对某些特定的请求进行过滤/拦截\n" +
                "\n" +
                "废话不多说，直接上代码：\n" +
                "```java\n" +
                "package top.easyblog.netty.http;\n" +
                "\n" +
                "import io.netty.bootstrap.ServerBootstrap;\n" +
                "import io.netty.buffer.ByteBuf;\n" +
                "import io.netty.buffer.Unpooled;\n" +
                "import io.netty.channel.*;\n" +
                "import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "import io.netty.channel.socket.SocketChannel;\n" +
                "import io.netty.channel.socket.nio.NioServerSocketChannel;\n" +
                "import io.netty.handler.codec.http.*;\n" +
                "import io.netty.util.CharsetUtil;\n" +
                "\n" +
                "import java.net.URI;\n" +
                "\n" +
                "/**\n" +
                " * @author HuangXin\n" +
                " * @since 2020/1/4 21:28\n" +
                " * 使用Netty写一个Http服务器并监听8080端口，\n" +
                " * 当浏览器发出http://localhost:8080请求后可以返回给浏览器信息“服务器收到请求！”\n" +
                " */\n" +
                "public class HttpServer {\n" +
                "\n" +
                "\t//监听的端口\n" +
                "\tprivate static final int DEFAULT_PORT = 8080;\n" +
                "\tprivate int port = -1;\n" +
                "\t//bossGroup 只负责处理accept事件\n" +
                "\tprivate EventLoopGroup bossGroup;\n" +
                "\t//workerGroup   负责具体的数据业务处理\n" +
                "\tprivate EventLoopGroup workerGroup;\n" +
                "\n" +
                "\n" +
                "\tpublic HttpServer() {\n" +
                "\t\tthis.bossGroup=new NioEventLoopGroup();\n" +
                "\t\tthis.workerGroup=new NioEventLoopGroup();\n" +
                "\t}\n" +
                "\n" +
                "\tpublic HttpServer(int port) {\n" +
                "\t\tthis();\n" +
                "\t\tthis.port = port;\n" +
                "\t}\n" +
                "\n" +
                "\tprivate ServerBootstrap initServer() {\n" +
                "\t\tServerBootstrap serverBootstrap = new ServerBootstrap();\n" +
                "\t\tserverBootstrap.group(bossGroup, workerGroup)    //设置两个线程组  \n" +
                "\t\t\t\t.channel(NioServerSocketChannel.class)    //设置服务器通道的类型\n" +
                "\t\t\t\t.option(ChannelOption.SO_BACKLOG, 128)    //设置线程队连接个数\n" +
                "\t\t\t\t.childOption(ChannelOption.SO_KEEPALIVE, true)  //设置连接保持活动\n" +
                "\t\t\t\t.childOption(ChannelOption.TCP_NODELAY, true)    //设置服务器非延迟发送\n" +
                "\t\t\t\t.childHandler(new HttpServerChannelInitializer());  ///给workerGroup的pipleline设置hanlder\n" +
                "\t\treturn serverBootstrap;\n" +
                "\t}\n" +
                "\n" +
                "\n" +
                "\tpublic void start() {\n" +
                "\t\ttry {\n" +
                "\t\t\tServerBootstrap serverBootstrap = initServer();\n" +
                "\t\t\tChannelFuture channelFuture;\n" +
                "\t\t\tif (port > 0) {\n" +
                "\t\t\t\tchannelFuture = serverBootstrap.bind(port).sync();\n" +
                "\t\t\t\t//监听异步过程bind()\n" +
                "\t\t\t\tchannelFuture.addListener((ChannelFutureListener) channelFuture1 -> {\n" +
                "\t\t\t\t\tif (channelFuture1.isSuccess()) {\n" +
                "\t\t\t\t\t\tSystem.out.println(\"服务器启动成功，正在监听\"+port+\"端口\");\n" +
                "\t\t\t\t\t} else {\n" +
                "\t\t\t\t\t\tSystem.out.println(\"服务器监听\" + port + \"端口失败,原因：\" + channelFuture1.cause());\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t});\n" +
                "\t\t\t} else {\n" +
                "\t\t\t\tchannelFuture = serverBootstrap.bind(DEFAULT_PORT).sync();\n" +
                "\t\t\t\t//监听异步过程bind()\n" +
                "\t\t\t\tchannelFuture.addListener((ChannelFutureListener) channelFuture1 -> {\n" +
                "\t\t\t\t\tif (channelFuture1.isSuccess()) {\n" +
                "\t\t\t\t\t\tSystem.out.println(\"服务器启动成功，正在监听\"+DEFAULT_PORT+\"端口\");\n" +
                "\t\t\t\t\t} else {\n" +
                "\t\t\t\t\t\tSystem.out.println(\"服务器监听\" + DEFAULT_PORT + \"端口失败,原因：\" + channelFuture1.cause());\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t});\n" +
                "\t\t\t}\n" +
                "\t\t\t//监听服务器的关闭\n" +
                "\t\t\tchannelFuture.channel().closeFuture().sync();\n" +
                "\t\t} catch (Exception e) {\n" +
                "\t\t\te.printStackTrace();\n" +
                "\t\t} finally {\n" +
                "\t\t\tif (bossGroup != null) {\n" +
                "\t\t\t\tbossGroup.shutdownGracefully();\n" +
                "\t\t\t}\n" +
                "\t\t\tif (workerGroup != null) {\n" +
                "\t\t\t\tworkerGroup.shutdownGracefully();\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "     /**\n" +
                "     *定义我们自己的初始化器HttpServerChannelInitializer \n" +
                "     */\n" +
                "\tstatic class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {\n" +
                "\n" +
                "\t\t@Override\n" +
                "\t\tprotected void initChannel(SocketChannel socketChannel) throws Exception {\n" +
                "\t\t\tChannelPipeline pipeline = socketChannel.pipeline();\n" +
                "\t\t\t//HttpSererCodec是netty提供一个基于Http编/解码器\n" +
                "\t\t\tpipeline.addLast(new HttpServerCodec());\n" +
                "\t\t\t//添加自定义的handler\n" +
                "\t\t\tpipeline.addLast(new HttpServerHandler());\n" +
                "\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "\t/**\n" +
                "\t * SimpleChannelInboundHandler是ChannelInboundHandler的子类 ，继承SimpleChannelInboundHandler也可以实现一个handler\n" +
                "\t * HttpObject 是客户端和服务器通讯的数据封装\n" +
                "\t */\n" +
                "\tstatic class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {\n" +
                "\n" +
                "\t\t//用于读取客户端的数据\n" +
                "\t\t@Override\n" +
                "\t\tprotected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {\n" +
                "\t\t\tif (httpObject instanceof HttpRequest) {\n" +
                "\t\t\t\tSystem.out.println(\"客户端地址：\" + ctx.channel().remoteAddress());\n" +
                "\t\t\t\tHttpRequest request= (HttpRequest) httpObject;\n" +
                "\t\t\t\tURI uri=new URI(request.uri());\n" +
                "\t\t\t\t//过滤部分请求，不响应\n" +
                "\t\t\t\tif(\"/static\".equals(uri.getPath())||\"/image\".equals(uri.getPath())){\n" +
                "\t\t\t\t\treturn;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\tSystem.out.println(\"请求路径：\" + ((HttpRequest) httpObject).uri());\n" +
                "\n" +
                "\t\t\t\tByteBuf content = Unpooled.copiedBuffer(\"服务器收到请求！Hello Client\",CharsetUtil.UTF_8);\n" +
                "\n" +
                "\t\t\t\t//构造一个http请求\n" +
                "\t\t\t\tDefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, \n" +
                "\t\t\t\t\t                                                           HttpResponseStatus.OK,\n" +
                "\t\t\t\t\t                                                           content);\n" +
                "\t\t\t\tresponse.headers().set(HttpHeaderNames.CONTENT_TYPE, \"text/plain\");\n" +
                "\t\t\t\tresponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());\n" +
                "\t\t\t\tctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "上面代码看似很长，但是会一点Netty的同学不难看懂，上面的代码无非干了下面几件事：\n" +
                "> 1、初始化连个线程池boosGroup和workerGroup，他们的实现类型都是`NioEventLoopGroup`，其中boosGroup只负责处理accept事件连接请求，workerGroup负责具体的数据业务处理\n" +
                "> 2. 使用ServerBootStrap进行初始化配置，其中初始化配置有：配置两个事件循环组(boosGroup和workerGroup)，配置服务器通道的类型为`NioServerSocketChannel`,配置初始化器以及一些其他的配置，具体的代码中有详细对注释。\n" +
                "> 3. 自定义初始化器，并在初始化其中指定自定义的Handler\n" +
                "> 4. 自定义Handler，对请求进行处理 并响应请求\n" +
                "\n" +
                "\n" +
                "#### 启动服务器演示效果\n" +
                "```java\n" +
                "package top.easyblog;\n" +
                "\n" +
                "import static org.junit.Assert.assertTrue;\n" +
                "\n" +
                "import org.junit.Test;\n" +
                "import top.easyblog.netty.http.HttpServer;\n" +
                "\n" +
                "/**\n" +
                " * Unit test for simple App.\n" +
                " */\n" +
                "public class AppTest {\n" +
                "    public static void main(String[] args) {\n" +
                "        new HttpServer().start();\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "启动服务器并在postman中输入http://localhost:8080，,运行结果截图如下：\n" +
                "<cneter><img src=\"https://i.loli.net/2020/01/05/Hj9fwTl5Le1diWp.png\" ></center>";
        new Markdown2HTMLParser().render2Html(mark);
    }
}
