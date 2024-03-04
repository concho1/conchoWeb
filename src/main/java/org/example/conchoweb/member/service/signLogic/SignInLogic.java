package org.example.conchoweb.member.service.signLogic;
import org.example.conchoweb.member.model.MemberDAO;
import org.example.conchoweb.member.model.MemberDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.example.conchoweb.member.service.signLogic.SignInResult.*;

@Service
public class SignInLogic {
    private final MemberDAO memberDAO;  //DAO : Data Access Object

    public SignInLogic(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    public SignInResult trySignIn(String email, String pw){
        Optional<MemberDTO> memberDTO = memberDAO.findByEmailAndPw(email, pw);
        if(memberDTO.isPresent()){  //DB에 값이 있으면
                                    return SIGN_IN_SUCCESSFUL;
        }else{                      //없으면
                                    return SIGN_IN_FAIL;
        }
    }
}

