package org.example.conchoweb.member.service.signLogic;
import org.example.conchoweb.member.model.MemberDAO;
import org.example.conchoweb.member.model.MemberDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import static org.example.conchoweb.member.service.signLogic.SignUpResult.*;
@Service
public class SignUpLogic {

    private final MemberDAO memberDAO;  //DAO : Data Access Object

    public SignUpLogic(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }


    public SignUpResult trySignUp(MemberDTO member){
        ArrayList<String> memberAllArr =  member.getAll();

        // null, 빈값 검사
        for(String memberInfo : memberAllArr){
           if(memberInfo == null)           return INFO_NULL;
           else if(memberInfo.isEmpty())    return INFO_MISSING;
        }
        // 비밀번호 형식 검사
        if(member.getPw().length() < 5 || member.getPw().length() > 20){
                                            return PASSWORD_FORMAT_FALSE;
        }

        // DB에 저장 시도
        try {
            memberDAO.save(member);
                                            return SIGN_UP_SUCCESSFUL;
        }catch (Exception e){
                                            return SIGN_UP_FAIL;
        }
    }
}
