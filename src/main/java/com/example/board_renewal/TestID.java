package com.example.board_renewal;

import com.example.board_renewal.Member;
import com.example.board_renewal.MemberService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TestID {

    private final MemberService memberService;

    public TestID(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 서버가 켜지자마자 실행되는 테스트 데이터
     */
    @PostConstruct
    public void init() {
        if (memberService.findByLoginId("test") != null) {
            return;
        }
        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("1234");
        member.setName("관리자");
        member.setRole("ADMIN");

        memberService.join(member);
        System.out.println("테스트 계정 생성 완료: id=test, pw=1234");
    }
}