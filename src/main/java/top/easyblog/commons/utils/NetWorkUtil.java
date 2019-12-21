package top.easyblog.commons.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public final class NetWorkUtil {

    private static Logger log = LoggerFactory.getLogger(NetWorkUtil.class);

    private static final String IP138 = "http://www.ip138.com/ips138.asp?ip=";

    /**
     * 解析客户端请求的真实ip地址
     *
     * @param request 请求
     * @return 客户端的ip地址
     */
    public static String getUserIp(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("request is null");
        }
        String ip = null;
        try {
            //Squid服务代理
            ip = request.getHeader("X-Forward-For");
            if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                //apache服务代理
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                //weblogic服务代理
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                //根据本地网卡获取ip地址
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip)) {
                    InetAddress inept = null;
                    try {
                        inept = InetAddress.getLocalHost();

                    } catch (UnknownHostException e) {
                        log.debug(e.getMessage());
                        //do noting
                    }
                    ip = null==inept?null:inept.getHostAddress();
                }
            }

            // 对于通过多个代理的情况，第一个不是unknown的ip为真实ip
            if (null != ip && ip.length() > 15) { //***.***.***.***
                final String[] ips = ip.split(",");
                for (final String str : ips) {
                    if (!"unknown".equalsIgnoreCase(str)) {
                        ip = str;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            ip = "";
        }

        //当使用localhost访问的时候获得的ip是0:0:0:0:0:0:0:1，可以把它转换成127.0.0.1
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 使用JSoup结合www.ip138.com抓取一个ip的物理地址信息
     *
     * @param request
     * @param ip
     * @return
     */
    public static String getLocation(HttpServletRequest request, String ip) {
        if (Objects.isNull(ip)&&Objects.isNull(request)) {
            log.error("ip或request不能为空");
            throw new RuntimeException("ip或request不能为空");
        }

        String location="未知地址";
        //通过请求头或者客户端的User-Agent
        String USERAGENT = request.getHeader("User-Agent");
        log.info(USERAGENT);
        try {
            //从ip138网站获得所需查询ip的物理地址
            Document doc = Jsoup.connect(IP138 + ip + "&action=2").timeout(80000).userAgent(USERAGENT).get();
            if (Objects.nonNull(doc)) {
                //JSoup支持使用类选择器来选择
                Element ul = doc.selectFirst(".ul1");
                if (Objects.nonNull(ul)) {
                    Element li = doc.selectFirst("li");
                    if (Objects.nonNull(li)) {
                        String text = li.text();
                        location=text.substring(5,text.length());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }
}

