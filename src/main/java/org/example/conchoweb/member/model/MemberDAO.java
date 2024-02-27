package org.example.conchoweb.member.model;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

/*
    Optional 객체는 null이 올때를 대비해 사용

 */
public interface MemberDAO extends CrudRepository<MemberDTO,String>{
    // email과 pw로 사용자 검색
    Optional<MemberDTO> findUserByEmailAndPw(String email,String pw);
    // email로 사용자 검색
    Optional<MemberDTO> findUserByEmail(String email);
}
