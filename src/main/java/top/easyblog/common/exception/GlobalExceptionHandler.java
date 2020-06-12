package top.easyblog.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局异常处理
 * @author huangxin
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log= LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        log.error("Request URL : {} ,Exception info : {}",request.getRequestURL(),e);

        if(null != AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class)){
            throw e;
        }
        ModelAndView mv=new ModelAndView();
        mv.addObject("url",request.getRequestURL());
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(null==statusCode){
            mv.addObject("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }else{
            mv.addObject("status",HttpStatus.valueOf(statusCode));
        }
        mv.addObject("time",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        mv.addObject("message",e.getMessage());
        mv.addObject("exception",e.getStackTrace());
        mv.setViewName("error/error");
        return mv;
    }



}
