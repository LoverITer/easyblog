package top.easyblog.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * service层日志切面
 * @author huangxin
 */
@Aspect
@Component
public class ServiceLogAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* top.easyblog.service.*.*(..))")
    public void log() {
    }

    @Before(value = "log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(classMethod);
    }

    @AfterReturning(pointcut = "log()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(classMethod + " has done successfully.");
    }

    @AfterThrowing(pointcut = "log()", throwing = "throwable")
    public void afterThrow(JoinPoint joinPoint, Throwable throwable) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.error(classMethod + " occurred an error :" + throwable.getMessage());
    }

    private static class ServiceLog {
        private String classMethod;
        private Object[] args;

        public ServiceLog( String classMethod, Object[] args) {
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "请求日志：" +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args);
        }
    }


}
