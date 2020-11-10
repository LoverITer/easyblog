package top.easyblog.util;


import com.alibaba.fastjson.JSONObject;
import com.vladsch.flexmark.util.html.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author huangxin
 */
@Slf4j
public final class NetWorkUtils {


    /***内网IP*/
    public static final String INTRANET_IP = getIntranetIp();
    /***外网IP*/
    public static final String INTERNET_IP = getInternetIp();
    /***外网ip字节数组*/
    public static final byte[] INTERNET_IP_BYTES = getInternetIpBytes();
    /***内网ip字节数组*/
    public static final byte[] INTRANET_IP_BYTES = getIntranetIpBytes();
    /*** IP138 ip查询接口*/
    private static final String IP138 = "http://www.ip138.com/ips138.asp?ip=";
    /***太平洋网IP查询接口*/
    private static final String PCONLINE = "http://whois.pconline.com.cn/ipJson.jsp?ip=";
    /**Ip解析未知 unknown*/
    private static final String UNKNOWN_IP="unknown";

    /**
     * 获得内网IP
     *
     * @return 内网IP
     */
    public static String getIntranetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得外网IP
     *
     * @return 外网IP
     */
    public static String getInternetIp() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements()) {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements()) {
                    ip = addrs.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && ip.isSiteLocalAddress()
                            && !ip.getHostAddress().equals(INTRANET_IP)) {
                        return ip.getHostAddress();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果没有外网IP，就返回内网IP
        return INTRANET_IP;
    }

    /**
     * 获得外网IP
     *
     * @return 外网IP
     */
    public static byte[] getInternetIpBytes() {
        return ipv4Address2BinaryArray(getInternetIp());
    }

    /**
     * 获取内网ip
     *
     * @return
     */
    public static byte[] getIntranetIpBytes() {
        return ipv4Address2BinaryArray(getIntranetIp());
    }

    /**
     * 将给定的用十进制分段格式表示的ipv4地址字符串转换成字节数组
     *
     * @param ipAdd
     * @return
     */
    public static byte[] ipv4Address2BinaryArray(String ipAdd) {
        byte[] binIP = new byte[4];
        String[] strs = ipAdd.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            binIP[i] = (byte) Integer.parseInt(strs[i]);
        }
        return binIP;
    }


    /**
     * 解析客户端请求的真实ip地址
     *
     * @param request 请求
     * @return 客户端的ip地址
     */
    public static String getInternetIPAddress(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("request is null");
        }
        String ip = null;
        try {
            //Nginx代理
            ip = request.getHeader("X-Real-IP");
            if (verifyIpAddress(ip)) {
                ip = request.getHeader("X-Forward-For");
                if(ip!=null) {
                    int index = ip.indexOf(',');
                    if (index != -1) {
                        ip = ip.substring(0, index);
                    }
                }
            }
            if (verifyIpAddress(ip)) {
                //apache服务代理
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (verifyIpAddress(ip)) {
                //weblogic服务代理
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (verifyIpAddress(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (verifyIpAddress(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            //没有代理，根据本地网卡获取ip地址
            if (verifyIpAddress(ip)) {
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
    private static boolean verifyIpAddress(String ip) {
        if(StringUtils.isEmpty(ip)||UNKNOWN_IP.equalsIgnoreCase(ip)){
            return false;
        }
        String ipReg="^((1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.){3}(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern ipModel=Pattern.compile(ipReg);
        return ipModel.matcher(ip).matches();
    }

    /***
     * 获取用户大概的地址
     * @param ip  ip
     * @return
     */
    public static String getLocation(String ip) {
        if (verifyIpAddress(ip)) {
            return "错误IP";
        }

        /**
         * 返回的数据格式： if(window.IPCallBack) {IPCallBack({"ip":"117.136.86.247","pro":"陕西省","proCode":"610000","city":"西安市","cityCode":"610100","region":"","regionCode":"0","addr":"陕西省西安市 移通","regionNames":"","err":""});}
         */

        String body = doGet(PCONLINE + ip);
        assert body != null;
        int start = body.indexOf("{IPCallBack(")+12;
        String jsonStr=body.substring(start,body.lastIndexOf(')'));
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        return (String) jsonObject.get("addr");
    }


    /**
     * 获取一个连接中的主机地址
     * @param href
     * @return
     */
    public static Optional<String> getHost(Attribute href) {
        String value = href.getValue();
        if(value==null){
            return Optional.empty();
        }
        int start = value.indexOf("://");
        if(start!=-1) {
            value = value.replaceFirst("://", ":::");
        }
        int end=value.indexOf('/',start);
        if(end!=-1) {
            value = value.substring(start + 3, end);
        }
        return Optional.of(value);
    }

    /**
     * 发送HTTP get请求
     *
     * @param url 请求的完整URL地址（包括请求参数）
     * @return json字符串
     */
    public static String doGet(String url) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        // 发送了一个http请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            // 如果响应200成功,解析响应结果
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 获取响应的内容
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String doGet(String url, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        headers.forEach(httpGet::setHeader);
        // 发送了一个http请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            // 如果响应200成功,解析响应结果
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 获取响应的内容
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 发送HTTP POST请求
     *
     * @param url           请求的URL地址
     * @param postJsonParam post请求参数
     * @return json字符串
     */
    public static String doPost(String url, String postJsonParam) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        //设置请求头，参数格式为json
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
        StringEntity stringEntity = new StringEntity(postJsonParam, "utf-8");
        httpPost.setEntity(stringEntity);
        // 发送了一个http请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            // 如果响应200成功,解析响应结果
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 获取响应的内容
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将字符串转换成map
     *
     * @param responseEntity
     * @return
     */
    public static Map<String, String> getMap(String responseEntity) {
        Map<String, String> map = new HashMap<>(16);
        if (StringUtils.isEmpty(responseEntity)) return map;
        // 以&来解析字符串
        String[] result = responseEntity.split("\\&");

        for (String str : result) {
            // 以=来解析字符串
            String[] split = str.split("=");
            // 将字符串存入map中
            if (split.length == 1) {
                map.put(split[0], null);
            } else {
                map.put(split[0], split[1]);
            }

        }
        return map;
    }

    /**
     * 通过json获得map
     *
     * @param responseEntity
     * @return
     */
    public static Map<String, String> getMapByJson(String responseEntity) {
        Map<String, String> map = new HashMap<>();
        // 阿里巴巴fastjson  将json转换成map
        JSONObject jsonObject = JSONObject.parseObject(responseEntity);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            // 将obj转换成string
            String value = String.valueOf(entry.getValue());
            map.put(key, value);
        }
        return map;
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(NetWorkUtils.doGet("https://www.easyblog.top"));
        String s = NetWorkUtils.doGet("http://zhuanti.ip3q.com/");
    }

}

