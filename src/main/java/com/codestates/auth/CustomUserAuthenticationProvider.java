package com.codestates.auth;

import com.codestates.member.Member;
import com.codestates.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/** 접속 요청 유저가 가입된 해당 유저가 정말 맞는지 검증하는 클래스 **/
@RequiredArgsConstructor
@Component
public class CustomUserAuthenticationProvider implements AuthenticationProvider {

    //DB에 저장되어 있는 Credential (해싱된) 과 요청시 들어 온 Credential (해싱 안 된) 비교를 위한 인코더,
    private final PasswordEncoder passwordEncoder;

    //멤버의 처리를 해주는 Service 객체, AuthenticationProvider 을 구현한 (현 클래스) CustomUserAuthenticationProvider 클래스로 인해
    //UserDetailsService 의 역할을 대체한다.
    private final MemberService memberService;

    //권한에 대한 처리를 담당하는 Util 객체
    private final CustomAuthorityUtils authorityUtils;

    //아직 인증되지 않은 Authentication 이 넘어 오고, 해당 메소드에서 인증과정을 거친 후
    //인증된 Authentication 을 리턴
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //아직 인증 되지 않은 Authentication 을 UsernamePasswordAuthenticationToken 로 형변환
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;

        //로그인 요청이 들어 온 Principal 정보를 가져오기 위해 username 을 구함
        String username = authToken.getName();

        Optional.ofNullable(username).orElseThrow(()-> new UsernameNotFoundException("Invalid Username or Password"));

        //Service 객체에서 멤버 정보 가져 옴
        Member findMember = memberService.findMember(username);

        //DB에 저장되어 있는 Credential
        String password = findMember.getPassword(); // DB에 저장되어 있는 크레덴셜

        //현재 로그인 요청 정보의 크레덴셜과 DB상 의 Credential 매치해봄
        verifyCredentials(authToken.getCredentials(), password);

        //해당 구간까지 왔다는것은 Credential 검증을 통과 한 상태 이므로
        //DB상에 권한 정보를 Spring Security에서 사용할 수 있는 권한정보 형태로 변환
        Collection<? extends GrantedAuthority> authorities = authorityUtils.getAuthority(findMember.getRoles());

        //최종적으로 인증이 된 Authentication 구현체를 반환
        return new UsernamePasswordAuthenticationToken(username, password, authorities);

    }

    private void verifyCredentials(Object credentials, String password) {
        if(!passwordEncoder.matches((String)credentials, password))
            throw new BadCredentialsException("Invalid username or password");
    }


    //현재 구현하는 사용자정의 AuthenticationProvider(CustomUserAuthenticationProvider)가
    //Username/Password 방식의 인증을 지원한다는 것을 Spring Security에게 알려주는 역할을 함.
    //해당 메소드를 정확히 구현해주지 않으면 인증과정이 진행되지 않음.
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
