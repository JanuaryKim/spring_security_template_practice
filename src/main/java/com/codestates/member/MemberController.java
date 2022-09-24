package com.codestates.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper mapper;

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @GetMapping("/register")
    public String registerMemberForm() {
        return "member-register";
    }

    @PostMapping("/register")
    public String registerMember(@Valid MemberDto.Post requestBody, Model model) {
        Member member = mapper.memberPostToMember(requestBody);
        Member createdMember = memberService.createMember(member);

        MemberDto.Response response = mapper.memberToMemberResponse(createdMember);
        model.addAttribute("member",response); //View로 데이터 전송

        System.out.println("Member Registration Successfully");
        return "login";
    }

    @GetMapping("/my-page")
    public String myPage() {
        return "my-page";
    }
}
