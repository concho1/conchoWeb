package org.example.conchoweb.member.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

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
    @Column(name="member_nickname")
    private String nickname;
    @Column(name="member_name")
    private String name;
    @Column(name="member_age")
    private String age;
    @Column(name="member_gender")
    private String gender;
    @Column(name="member_folder_id")
    private String folderId;

    public MemberDTO() {}
    public MemberDTO(String email, String pw,  String nickname, String name, String age, String gender) {
        this.email = email;
        this.pw = pw;
        this.nickname = nickname;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
    public ArrayList<String> getAll(){
        ArrayList<String> allVar = new ArrayList<>();
        allVar.add(this.email);
        allVar.add(this.pw);
        allVar.add(this.nickname);
        allVar.add(this.name);
        allVar.add(this.age);
        allVar.add(this.gender);
        return allVar;
    }

}
