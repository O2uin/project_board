package com.example.board_renewal;

import com.example.board_renewal.Member;
import com.example.board_renewal.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

    private final MemberService memberService;

    // 생성자 주입
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 1. 회원가입 폼 보여주기
    @GetMapping("/members/new")
    public String createForm() {
        return "member-form";
    }

    // 2. 실제 회원 저장하기
    @PostMapping("/members/new")
    public String create(Member member, Model model) {
        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("member", member); // ★ 입력했던 member 객체를 다시 담음
            return "member-form";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login-form"; // login-form.html 파일을 찾아감
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) { // ★ HttpServletRequest 추가

        Member loginMember = memberService.login(loginId, password);

        if (loginMember == null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 맞지 않습니다.");
            model.addAttribute("loginId", loginId); // ★ 입력했던 ID만 다시 담음 (비번은 보안상 보통 비워둠)
            return "login-form";
        }

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();
        // 세션에 로그인 회원 정보 보관
        session.setAttribute("loginMember", loginMember);
        return "redirect:/posts";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없으면 새로 만들지 마라
        if (session != null) {
            session.invalidate(); // 세션 날리기
        }
        return "redirect:/posts";
    }

    @GetMapping("/members/myPage")
    public String myPage(HttpServletRequest request, Model model) {
        // 1. 세션에서 로그인한 회원 정보 꺼내기
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 2. 로그인 안 되어 있으면 홈으로
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 3. DB에서 최신 정보 다시 읽어오기 (수정 사항 반영 등을 위해)
        Member member = memberService.findByLoginId(loginMember.getLoginId());
        model.addAttribute("member", member);

        return "myPage"; // 4. 마이페이지 뷰로 이동
    }

    // 1. 수정 화면 보여주기
    @GetMapping("/members/edit")
    public String editForm(@RequestParam String password, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) return "redirect:/login";

        // DB에서 실시간 비번 확인
        Member member = memberService.findByLoginId(loginMember.getLoginId());

        // 비번 틀리면 다시 마이페이지로 돌려보내기
        if (!member.getPassword().equals(password)) {
            // (선택) 에러 메시지를 마이페이지에 띄우고 싶다면 추가 로직 필요
            return "redirect:/members/myPage";
        }

        // 맞으면 수정 화면으로 이동
        model.addAttribute("member", member);
        return "editMember";
    }

    // 2. 수정 실행하기
    @PostMapping("/members/edit")
    public String edit(@RequestParam String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) return "redirect:/login";

        // 서비스에 이름 변경 시키기
        memberService.updateMemberName(loginMember.getId(), name);

        // 세션에 저장된 이름도 같이 바꿔줘야 상단 바 닉네임이 바로 바뀜!
        loginMember.setName(name);

        return "redirect:/members/myPage"; // 수정 끝나면 마이페이지로 이동
    }

    @PostMapping("/members/withdraw")
    public String withdraw(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            // 1. DB에서 삭제
            memberService.deleteMember(loginMember.getId());
            // 2. 세션 무효화 (로그아웃 처리)
            session.invalidate();
        }

        return "redirect:/posts"; // 홈으로 이동
    }
}