package com.example.board_renewal;

import com.example.board_renewal.Member;
import com.example.board_renewal.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    public Long join(Member member) {
        // 중복 아이디 검증
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 아이디입니다.");
                });

        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 로그인 확인
     * @return 로그인 성공 시 Member 객체, 실패 시 null (또는 Optional.empty)
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    // MemberService 클래스 안에 추가하세요!
    public Member findByLoginId(String loginId) {
        // 리포지토리한테 시켜서 찾아온 다음, 없으면 null을 돌려줍니다.
        return memberRepository.findByLoginId(loginId)
                .orElse(null);
    }
    @Transactional
    public void updateMemberName(Long id, String newName) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("회원이 없습니다."));
        member.setName(newName);
    }

    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}