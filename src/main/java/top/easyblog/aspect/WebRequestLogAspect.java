package top.easyblog.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.easyblog.commons.utils.NetWorkUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class WebRequestLogAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* top.easyblog.controller.*.*(..))")
    public void log() {}


    @Before(value = "log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String ip = NetWorkUtil.getUserIp(request);
        String url = request.getRequestURL().toString();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(new RequestLog(url, ip+" "+NetWorkUtil.getLocation(request,ip), classMethod, joinPoint.getArgs()).toString());
    }

    @AfterReturning(pointcut = "log()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(classMethod + " has done successfully.result value {}",result);
    }

    @AfterThrowing(pointcut = "log()", throwing = "throwable")
    public void afterThrow(JoinPoint joinPoint, Throwable throwable) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.error(classMethod + " occurred an error :" + throwable.getMessage());
    }

    private static class RequestLog {
        private String url;
        private String ipInfo;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ipInfo, String classMethod, Object[] args) {
            this.url = url;
            this.ipInfo = ipInfo;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "请求日志：" +
                    "url='" + url + '\'' +
                    ", ipInfo='" + ipInfo + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args);
        }
    }


}
