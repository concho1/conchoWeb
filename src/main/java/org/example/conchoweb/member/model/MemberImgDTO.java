package org.example.conchoweb.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="member_img_table")
public class MemberImgDTO {
    @Id
    @Column(name="img_id")
    private int id;
    @Column(name="img_email")
    private String email;
    @Column(name="img_latitude")
    private String latitude;
    @Column(name="img_longitude")
    private String longitude;
    @Column(name="img_date")
    private String date;
    public MemberImgDTO(){}
    public MemberImgDTO(String email, String latitude, String longitude, String date) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
}
