package org.easyblog.controller;


import org.easyblog.bean.User;
import org.easyblog.bean.UserComment;
import org.easyblog.config.Result;
import org.easyblog.service.impl.CommentServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
@RequestMapping(value = "/comment")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @ResponseBody
    @PostMapping(value = "/publish",produces = "application/json;charset=UTF-8")
    public Result publishComment(@RequestBody UserComment comment, HttpSession session){
        Result result = new Result();
        result.setSuccess(false);
        //登录用户就是发评论者，如果没有登录不可以发评论
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)&&Objects.nonNull(comment)){
           comment.setCommentSend(user.getUserId());
           if(comment.getPid()==0){
               comment.setPid(null);
           }
            int re = commentService.save(comment);
            if (re>0){
                result.setSuccess(true);
                result.setMsg("OK");
            }
        }
        return result;
    }


}
