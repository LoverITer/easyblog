package top.easyblog.log;

import java.util.Arrays;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/10/12 17:49
 */
public class RequestLog extends AbstractLogBody{


    private String url;
    private String ipInfo;

    public RequestLog(String url, String ipInfo, String classMethod, Object[] args) {
        super(classMethod,args);
        this.url = url;
        this.ipInfo = ipInfo;
    }

    @Override
    public String toString() {
        return "请求：" + url + '\'' +
                ", 客户端IP地址:'" + ipInfo + '\'' +
                ", 调用了方法:'" + classMethod + '\'' +
                ", 入参:" + Arrays.toString(args);
    }

}
