package org.easyblog.handler.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DefaultExceptionHandler {

    private final Logger log= LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e){
        ModelAndView mv=new ModelAndView();
        log.error("Request URL : {} ,Exception : {}",request.getRequestURL(),e);
        mv.addObject("url",request.getRequestURL());
        mv.addObject("statusCode:",mv.getStatus());
        mv.addObject("Exception:",e);
        mv.setViewName("/error/5xx");
        return mv;
    }



}
