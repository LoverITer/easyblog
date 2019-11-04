package org.easyblog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/manage/comment")
public class CommentAdminController {

    private static final String PREFIX="/admin/comment_manage/";

    @GetMapping(value = "/list")
    public String commentListPage(){
        return PREFIX+"comment-manage";
    }


}
