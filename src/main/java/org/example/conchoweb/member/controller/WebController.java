package org.example.conchoweb.member.controller;

import jakarta.servlet.http.HttpSession;
import org.example.conchoweb.member.model.MemberDAO;
import org.example.conchoweb.member.model.MemberDTO;
import org.example.conchoweb.member.service.signLogic.SignInLogic;
import org.example.conchoweb.member.service.signLogic.SignInResult;
import org.example.conchoweb.member.service.signLogic.SignUpLogic;
import org.example.conchoweb.member.service.signLogic.SignUpResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

/*
    classpath:/META-INF/resources/
    classpath:/resources/
    "classpath:/static/"
    "classpath:/public/"
    spring boot 의 path => 현제 프로젝트의 path는 우선순위에 따라 lasspath:/resources/ 로 설정되어 있음
 */
@Controller
public class WebController {

    private final MemberDAO memberDAO;
    public WebController(MemberDAO memberDAO) { // 생성자를 통한 MemberDAO 주입
        this.memberDAO = memberDAO;
    }
    @GetMapping("/index")
    public String getIndex(){
        return "index";
    }
    // 비회원 ==================================================================================
    @GetMapping("/openPage/pageHome")
    public String getHome(){
        return "openPage/pageHome";
    }
    // 회원가입 페이지를 위한 메서드

    @GetMapping("/openPage/pageCommunityTm")
    public String getPageCommunityTm(){
        return "openPage/pageCommunityTm";
    }
    @GetMapping("/")
    public String getFirstHome(){
        return "openPage/pageHome";
    }

    // email 중복체크 api
    @PostMapping("/api/check-email")
    @ResponseBody()
    public Map<String, Boolean> checkEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        boolean isAvailable = !this.memberDAO.existsByEmail(email);// db에 사용중인 이메일 없을 경우
        return Collections.singletonMap("isAvailable", isAvailable);
    }
    @GetMapping("/openPage/pageLogIn")
    public String getPageLogin(Model model){
        // MemberDTO 객체를 생성하고 모델에 추가
        model.addAttribute("memberDTO", new MemberDTO());
        return "openPage/pageLogIn";
    }
    @GetMapping("/openPage/pageSignUp")
    public String showSignUpForm(Model model) {
        // MemberDTO 객체를 생성하고 모델에 추가
        model.addAttribute("memberDTO", new MemberDTO());
        return "openPage/pageSignUp"; // 회원가입 폼 뷰의 이름
    }
    // 회원가입
    @PostMapping("/api/signup")
    public String signUp(@ModelAttribute MemberDTO member){

        SignUpLogic signUpLogic = new SignUpLogic(memberDAO);
        // 회원가입 시도
        SignUpResult resultEnum = signUpLogic.trySignUp(member);

        // 나중에 회원가입 오류별 로직 추가하기
        switch(resultEnum){
            case SIGN_UP_SUCCESSFUL -> {
                return "redirect:/memberPage/pageMemberHome";   //redirect 로 중복 회원가입 폼 제출 방지
            }
            default -> {
                return "openPage/pageSignUp";
            }
        }
    }
    // 로그인
    @PostMapping("/api/signIn")
    public String signIn(@ModelAttribute MemberDTO member, HttpSession session){

        SignInLogic signUpLogic = new SignInLogic(memberDAO);
        // 로그인 시도 시도
        SignInResult resultEnum = signUpLogic.trySignIn(member.getEmail(), member.getPw());

        // 나중에 로그인 오류별 로직 추가하기
        switch(resultEnum){
            case SIGN_IN_SUCCESSFUL -> {
                //Undertow(spring boot 내장 서버) 의 경우 보통 timeout 이 30분
                //세션에 이메일 정보를 저장
                session.setAttribute("memberEmail", member.getEmail());
                return "redirect:/memberPage/pageMemberHome";   //redirect 로 중복 로그인 폼 제출 방지
            }
            default -> {
                return "openPage/pageSignUp";
            }
        }
    }

    //회원 ==================================================================================
    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 정보를 삭제하여 로그아웃 처리
        return "redirect:/openPage/pageHome"; // 로그아웃 후 리다이렉트할 페이지
    }
    @GetMapping("/memberPage/pageMemberHome")
    public String getMemberHome(HttpSession session, Model model){  //모델은 thymeleaf 를 통해 html 로 전달
        String memberEmail = String.valueOf(session.getAttribute("memberEmail")); // session 의 반환 타입은 Object 따라서 String 타입으로 변환
        if(memberEmail == null || memberEmail.isEmpty()){
            return "redirect:/openPage/pageHome";
        }else{
            return "memberPage/pageMemberHome";
        }
    }

}
