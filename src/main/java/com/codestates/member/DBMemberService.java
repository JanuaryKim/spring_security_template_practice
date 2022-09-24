package com.codestates.member;

import com.codestates.auth.CustomAuthorityUtils;
import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
public class DBMemberService implements MemberService {

    //멤버를 저장하기 위한 레포지토리
    private final MemberRepository memberRepository;

    //비밀번호를 해싱하여 저장하기 위한 인코더
    private final PasswordEncoder passwordEncoder;

    //멤버의 권한을 설정하기 위한 사용자정의 유틸 클래스
    private final CustomAuthorityUtils customAuthorityUtils;


    @Override
    public Member createMember(Member member) {

        verifyExistsMember(member.getEmail());

        //사용자가 입력하여 보내온 비밀번호를 해싱
        String encodedPwd = passwordEncoder.encode(member.getPassword());

        //해싱한 비밀번호로 엔티티에 다시 세팅
        member.setPassword(encodedPwd);

        //권한 설정
        List<String> authority = customAuthorityUtils.createAuthority(member.getEmail());
        member.setRoles(authority);

        //멤버 저장
        Member savedMember = memberRepository.save(member);

        return savedMember;
    }

    //존재하는 멤버인지 검증, 유니크해야하는 이메일이 존재하다면 가입 불가 멤버로써 에러
    private void verifyExistsMember(String email) {

        memberRepository.findByEmail(email).ifPresent(optionalMember -> {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS); });
    }

    //멤버의 로그인 요청시, 요청과 함께 들어온 Principal (ID) 로 검색하여 해당 하는 멤버 정보를 가져오는 메소드
    public Member findMember(String username) {

        Optional<Member> optionalMember = memberRepository.findByEmail(username);

        Member findMember = optionalMember.orElseThrow(() -> {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        });

        return findMember;
    }
}
