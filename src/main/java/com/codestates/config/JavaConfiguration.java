package com.codestates.config;

import com.codestates.auth.CustomAuthorityUtils;
import com.codestates.member.DBMemberService;
import com.codestates.member.InMemoryMemberService;
import com.codestates.member.MemberRepository;
import com.codestates.member.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class JavaConfiguration {
//    @Bean
//    public MemberService inMemoryMemberService() {
//        return new InMemoryMemberService();
//    }


    @Bean
    public MemberService DBMemoryMemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            CustomAuthorityUtils authorityUtils) {
        return new DBMemberService(memberRepository, passwordEncoder, authorityUtils);
    }
}
