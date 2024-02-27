package org.example.conchoweb.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
    classpath:/META-INF/resources/
    classpath:/resources/
    "classpath:/static/"
    "classpath:/public/"
    spring boot 의 path => 현제 프로젝트의 path는 우선순위에 따라 lasspath:/resources/ 로 설정되어 있음
 */
@Controller
public class WebController {
    @GetMapping("/openPage/pageHome")
    public String getHome(){
        return "openPage/pageHome";
    }
    @GetMapping("/openPage/pageSignUp")
    public String getPageSignUp(){
        return "openPage/pageSignUp";
    }
    @GetMapping("/openPage/pageLogin")
    public String getPageLogin(){
        return "openPage/pageLogIn";
    }
    @GetMapping("/openPage/pageCommunityTm")
    public String getPageCommunityTm(){
        return "openPage/pageCommunityTm";
    }
    @GetMapping("/index")
    public String getIndex(){
        return "index";
    }

}
