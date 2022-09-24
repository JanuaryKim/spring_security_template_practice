package com.codestates.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/** 사용자 권한에 대한 처리를 담당하는 클래스 **/
@Component
public class CustomAuthorityUtils {

    @Value("${mail.address.admin}")
    private String adminEmail;

    private final List<String> ADMIN_ROLE = new ArrayList<>(List.of("ADMIN", "USER"));
    private final List<String> USER_ROLE = new ArrayList<>(List.of("USER"));


    /** 최초 가입자에게 권한을 만들어 주는 메소드 **/
    public List<String> createAuthority(String email) {

        if (email.equals(adminEmail)) {
            return ADMIN_ROLE;
        }

        return USER_ROLE;
    }

    /** DB 상의 사용자 권한 정보를 Spring Security가 인식하는 권한 정보로 변환 하여 리턴해주는 메소드 **/
    public List<GrantedAuthority> getAuthority(List<String> authority) {

        //“ADMIN"으로 넘겨주면 안되고 “ROLE_USER" 또는 “ROLE_ADMIN" 형태로 넘겨주어야 한다!! 즉, ROLE_ 이 꼭 들어가야 한다
        return authority.stream().map(str-> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList());

    }
}
