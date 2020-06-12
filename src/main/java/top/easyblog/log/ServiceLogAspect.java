package top.easyblog.log;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * 系统日志记录
 * @author huangxin
 */
@Slf4j
@Aspect
@Component
public class ServiceLogAspect {


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
            return "request log{：" +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) + "}";
        }
    }


}
