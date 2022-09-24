package com.codestates.auth;

import com.codestates.member.Member;
import com.codestates.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;


// AuthenticationProvider 를 구현한 CustomUserAuthenticationProvider 클래스로 인해 현재는 사용되지 않음!!!
/** 유저 정보 (username) 를 가지고 user 정보 저장소에서 유저 정보를 가지고 와서 UserDetails를 만들기 위함 핵심 인터페이스 **/
@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;
    private final CustomAuthorityUtils authorityUtils;


    /** 유저정보를 load 하는 메소드로써, load 해오는곳은 해당 메소를 오버라이딩 함으로써 임의로 지정이 가능하다 **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member findMember = memberService.findMember(username);



        return new CustomUserDetailsMember(findMember);
    }


    /** 앱에서 사용하는 사용자 Entity와 Spring Security에서 인식할 수 있는 UserDetails 를 상속, 구현하여  UserDetailsService
     *  에서 사용하는 통합 객체로써의 역할을 함 **/
    private final class CustomUserDetailsMember extends Member implements UserDetails {


        public CustomUserDetailsMember(Member member) {
            setMemberId(member.getMemberId());
            setFullName(member.getFullName());
            setEmail(member.getEmail());
            setPassword(member.getPassword());
            setRoles(member.getRoles());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.getAuthority(getRoles());
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
