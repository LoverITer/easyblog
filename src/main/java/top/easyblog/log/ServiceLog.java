package top.easyblog.log;

import java.util.Arrays;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/10/12 19:01
 */
public class ServiceLog extends AbstractLogBody {

    public ServiceLog(String classMethod, Object[] args) {
        super(classMethod, args);
    }


    @Override
    public String toString() {
        return "调用服务方法：" + classMethod + '\'' +
                ", 入参：" + Arrays.toString(args);
    }

}
