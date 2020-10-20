package top.easyblog.log;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 系统日志记录
 * @author huangxin
 */
@Slf4j
@Aspect
@Component
public class ServiceLogAspect {


    @Pointcut(value = "execution(* top.easyblog.web.service.*.*(..))")
    public void log() {
    }

    @Before(value = "log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(String.valueOf(new ServiceLog(classMethod,joinPoint.getArgs())));
    }

    @AfterReturning(pointcut = "log()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(classMethod + " 执行成功. 返回值："+result);
    }

    @AfterThrowing(pointcut = "log()", throwing = "throwable")
    public void afterThrow(JoinPoint joinPoint, Throwable throwable) {
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.error(classMethod + " 执行过程发生异常:" + throwable.getMessage());
    }



}
