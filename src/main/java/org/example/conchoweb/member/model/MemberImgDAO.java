package org.example.conchoweb.member.model;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface MemberImgDAO extends CrudRepository<MemberImgDTO, String> {
    ArrayList<MemberImgDTO> findByEmail(String email);
}
