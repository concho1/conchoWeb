package org.example.member.repository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name="member_db")
@Table(name="member_table")
public class MemberDTO {
    @Id
    @Column(name="member_email")
    private String email;
    @Column(name = "member_pw")
    private String pw;
    @Column(name="member_name")
    private String name;
    @Column(name="member_age")
    private String age;
    @Column(name="member_age")
    private String gender;
    public MemberDTO() {}
    public MemberDTO(String email, String pw, String name, String age, String gender) {
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

}
