package top.easyblog.log;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.easyblog.util.NetWorkUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * controller层日志切面
 *
 * @author huangxin
 */
@Slf4j
@Aspect
@Component
public class WebPublicRequestLogAspect {


    @Pointcut(value = "execution(* top.easyblog.web.controller.*.*(..))")
    public void log() { }


    @Before( value="log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String ip = NetWorkUtils.getUserIp(request);
        String url = request.getRequestURL().toString();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(new RequestLog(url, ip + " " + NetWorkUtils.getLocation(ip), classMethod, joinPoint.getArgs()).toString());
    }

    @AfterReturning(pointcut = "log()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(classMethod + " 成功执行结束.返回值： {}", result);
    }

    @AfterThrowing(pointcut = "log()", throwing = "throwable")
    public void afterThrow(JoinPoint joinPoint, Throwable throwable) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.error(classMethod + " 执行过程发生异常:" + throwable.getMessage());
    }


}
