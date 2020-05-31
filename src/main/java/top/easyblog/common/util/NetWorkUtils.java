package top.easyblog.common.util;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author huangxin
 */
@Slf4j
public final class NetWorkUtils {

    private static final String IP138 = "http://www.ip138.com/ips138.asp?ip=";
    /**Ip解析未知 unknown*/
    private static final String UNKNOWN_IP="unknown";

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
            //Nginx代理
            ip=request.getHeader("X-Real-IP");
            if(!isIpValid(ip)){
                ip = request.getHeader("X-Forward-For");
                int index = ip.indexOf(',');
                if (index != -1) {
                   ip= ip.substring(0, index);
                }
            }

            if (!isIpValid(ip)) {
                //apache服务代理
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (!isIpValid(ip)) {
                //weblogic服务代理
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (!isIpValid(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (!isIpValid(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            //没有代理，根据本地网卡获取ip地址
            if (!isIpValid(ip)) {

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
            if (null != ip && ip.length() > 15) {
                //***.***.***.***
                final String[] ips = ip.split(",");
                for (final String str : ips) {
                    if (!UNKNOWN_IP.equalsIgnoreCase(str)) {
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
     * 测试ip地址是否是一个有效的Ip
     * @param ip  ip
     * @return 如果有效返回true ,否者返回false
     */
    private static boolean isIpValid(String ip){
        if(StringUtils.isEmpty(ip)||UNKNOWN_IP.equalsIgnoreCase(ip)){
            return false;
        }
        String ipReg="^((1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.){3}(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern ipModel=Pattern.compile(ipReg);
        return ipModel.matcher(ip).matches();
    }

    /**
     * 使用JSoup结合www.ip138.com抓取一个ip的物理地址信息
     *
     * @param request 请求
     * @param ip     IP地址
     * @return
     */
    public static String getLocation(HttpServletRequest request, String ip) {
        if (Objects.isNull(ip)&&Objects.isNull(request)) {
            log.error("ip或request不能为空");
            throw new RuntimeException("ip或request不能为空");
        }

        String location="未知地址";
        //通过请求头或者客户端的User-Agent
        String userAgent = request.getHeader("User-Agent");
        try {
            if (Objects.nonNull(userAgent)) {
                //从ip138网站获得所需查询ip的物理地址
                Document doc = Jsoup.connect(IP138 + ip + "&action=2").timeout(50000).userAgent(userAgent).get();
                if (Objects.nonNull(doc)) {
                    //JSoup支持使用类选择器来选择
                    Element ul = doc.selectFirst(".ul1");
                    if (Objects.nonNull(ul)) {
                        Element li = doc.selectFirst("li");
                        if (Objects.nonNull(li)) {
                            String text = li.text();
                            location = text.substring(5);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return location;
    }

}

