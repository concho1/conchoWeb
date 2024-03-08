package org.example.conchoweb.member.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import org.example.conchoweb.member.model.*;
import org.example.conchoweb.member.service.imgLogic.FileDownloadLogic;
import org.example.conchoweb.member.service.imgLogic.FileUploadLogic;
import org.example.conchoweb.member.service.signLogic.SignInLogic;
import org.example.conchoweb.member.service.signLogic.SignInResult;
import org.example.conchoweb.member.service.signLogic.SignUpLogic;
import org.example.conchoweb.member.service.signLogic.SignUpResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.conchoweb.member.model.MemberDTO;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

/*
    classpath:/META-INF/resources/
    classpath:/resources/
    "classpath:/static/"
    "classpath:/public/"
    spring boot 의 path => 현제 프로젝트의 path는 우선순위에 따라 lasspath:/resources/ 로 설정되어 있음


    GET, POST, PUT, DELETE  ==> REST API 를 지향
    GET    : 웹 페이지를 보여주거나, 데이터베이스에서 정보를 읽어 클라이언트에게 전달할 때 사용
    POST   : 사용자 입력을 받아 새로운 데이터를 생성하고 데이터베이스에 저장할 때 사용
    PUT    : 기존 데이터를 클라이언트로부터 받은 새 데이터로 교체할 때 사용합니다.
    DELETE : 지정된 ID의 데이터를 데이터베이스에서 삭제할 때 사용합니다.

 */
@Controller
public class WebController {

    private final MemberDAO memberDAO;
    private final MemberImgDAO memberImgDAO;
    public WebController(MemberDAO memberDAO, MemberImgDAO memberImgDAO) { // 생성자를 통한 MemberDAO 주입
        this.memberDAO = memberDAO;
        this.memberImgDAO = memberImgDAO;
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


    @GetMapping("/openPage/pageLogIn")
    public String getPageLogin(Model model){
        // MemberDTO 객체를 생성하고 모델에 추가
        model.addAttribute("memberDTO", new MemberDTO());
        model.addAttribute("loginResult",null);
        return "openPage/pageLogIn";
    }
    @GetMapping("/openPage/pageSignUp")
    public String showSignUpForm(Model model) {
        // MemberDTO 객체를 생성하고 모델에 추가
        model.addAttribute("memberDTO", new MemberDTO());
        return "openPage/pageSignUp"; // 회원가입 폼 뷰의 이름
    }

    // email 중복체크 api
    @PostMapping("/api/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        boolean isAvailable = !this.memberDAO.existsByEmail(email);
        return Collections.singletonMap("isAvailable", isAvailable);
    }

    // 회원가입
    @PostMapping("/api/signup")
    public String signUp(@ModelAttribute MemberDTO member) throws GeneralSecurityException, IOException {

        SignUpLogic signUpLogic = new SignUpLogic(memberDAO, memberImgDAO);
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
    public String signIn(@ModelAttribute MemberDTO member, HttpSession session, Model model){

        SignInLogic signUpLogic = new SignInLogic(memberDAO);
        // 로그인 시도 시도
        SignInResult resultEnum = signUpLogic.trySignIn(member.getEmail(), member.getPw());

        model.addAttribute("loginResult", String.valueOf(resultEnum));
        // 나중에 로그인 오류별 로직 추가하기
        switch(resultEnum){
            case SIGN_IN_SUCCESSFUL -> {
                //Undertow(spring boot 내장 서버) 의 경우 보통 timeout 이 30분
                //세션에 이메일 정보를 저장
                session.setAttribute("memberEmail", member.getEmail());

                return "redirect:/memberPage/pageMemberHome";   //redirect 로 중복 로그인 폼 제출 방지
                //return "redirect:/openPage/pageLogIn";   //redirect 로 중복 로그인 폼 제출 방지
            }
            default -> {
                return "openPage/pageLogIn";
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

    // 회원 전용 홈페이지
    @GetMapping("/memberPage/pageMemberHome")
    public String getMemberHome(HttpSession session, Model model) throws GeneralSecurityException, IOException {  //모델은 thymeleaf 를 통해 html 로 전달
        String memberEmail = String.valueOf(session.getAttribute("memberEmail")); // session 의 반환 타입은 Object 따라서 String 타입으로 변환
        System.out.println(memberEmail);

        if(memberEmail.equals("null") || memberEmail.isBlank()){
            return "redirect:/openPage/pageHome";
        }else{

            Optional<MemberDTO> memberDTO = memberDAO.findUserByEmail(memberEmail);
            if(memberDTO.isPresent()){
                MemberDTO member = memberDTO.get();
                model.addAttribute("nickname", member.getNickname());
            }
            model.addAttribute("email", memberEmail);

            FileDownloadLogic fileDownloadLogic = new FileDownloadLogic(memberDAO);
            List<File> files = fileDownloadLogic.getDriveFiles(memberEmail);


            /*
            List<String> urlList = fileDownloadLogic.getDriveFileLinks(memberEmail); // 이전 링크 구문

            for(int i=1; i<=urlList.size(); i++){
                System.out.println(urlList.get(i-1));
                model.addAttribute("url"+ i, urlList.get(i-1));
            }
            */



            return "memberPage/pageMemberHome";
        }
    }

    // 이미지 업로드 페이지
    @GetMapping("/memberPage/pageImgUpload")
    public String getUploadedFolder(HttpSession session, Model model) throws GeneralSecurityException, IOException {
        String memberEmail = String.valueOf(session.getAttribute("memberEmail"));

        if(memberEmail.equals("null") || memberEmail.isBlank()){
            return "redirect:/openPage/pageHome";
        }

        Optional<MemberDTO> memberDTO = memberDAO.findUserByEmail(memberEmail);
        if(memberDTO.isPresent()){
            MemberDTO member = memberDTO.get();
            model.addAttribute("nickname", member.getNickname());
        }

        model.addAttribute("file", new Object());




        return "memberPage/pageImgUpload";
    }


    // 이미지 업로드 api
    @PostMapping("/upload-img")
    public String singleFileUpload(@RequestParam("file") MultipartFile multipartFile, RedirectAttributes redirectAttributes, HttpSession session) throws GeneralSecurityException, IOException {
        String memberEmail = String.valueOf(session.getAttribute("memberEmail"));
        FileUploadLogic fileLogic = new FileUploadLogic(memberDAO, memberImgDAO);
        if(memberEmail.equals("null") || memberEmail.isBlank()){
            return "redirect:/openPage/pageHome";
        }

        if (multipartFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "파일을 선택해 주세요.");
            return "redirect:/memberPage/pageImgUpload"; // 파일이 비어있으면 업로드 상태 페이지로 리다이렉트
        }

        if(fileLogic.tryUpload(multipartFile, memberEmail)) {      // 파일 업로드 성공
            redirectAttributes.addFlashAttribute("message", "성공적으로 업로드 되었습니다 '" + multipartFile.getOriginalFilename() + "'");
        }else{
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패.");
            return "redirect:/memberPage/pageImgUpload"; // 파일이 업로드 실패시 페이지로 리다이렉트
        }

        return "redirect:/memberPage/pageImgUpload"; // 업로드 후 상태 페이지로 리다이렉트
    }

    @GetMapping("/memberPage/mapTest")
    public String getMapTest(HttpSession session, Model model){
        String memberEmail = String.valueOf(session.getAttribute("memberEmail"));
        if(memberEmail.equals("null") || memberEmail.isBlank()){
            return "redirect:/openPage/pageHome";
        }
        Optional<MemberDTO> memberDTO = memberDAO.findUserByEmail(memberEmail);
        if(memberDTO.isPresent()){
            MemberDTO member = memberDTO.get();
            model.addAttribute("nickname", member.getNickname());
        }
        ArrayList<Double[]> locations = new ArrayList<>();
        ArrayList<MemberImgDTO> memberImageInfos = memberImgDAO.findByEmail(memberEmail);
        for(MemberImgDTO memberImageInfo : memberImageInfos){
            Double[] location = new Double[2];
            location[0] = Double.valueOf(memberImageInfo.getLatitude());
            location[1] = Double.valueOf(memberImageInfo.getLongitude());
            System.out.println("위도,경도 : "+location[0] + ",  " + location[1]);
            locations.add(location);
        }
        // Gson 객체를 사용하여 locations 데이터를 JSON 문자열로 변환
        Gson gson = new Gson();
        String jsonLocations = gson.toJson(locations);

        // 변환된 JSON 문자열을 모델에 추가
        model.addAttribute("jsonLocations", jsonLocations);
        return "memberPage/mapTest";
    }

}
