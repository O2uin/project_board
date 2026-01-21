package com.example.board_renewal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // boardId -> postId로 이름 변경해서 혼란 방지!
    public boolean toggleLike(Long postId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        // 이미 좋아요를 눌렀는지 확인
        Optional<Like> alreadyLike = likeRepository.findByMemberAndPost(member, post);

        if (alreadyLike.isPresent()) {
            likeRepository.delete(alreadyLike.get()); // 취소
            return false; // "나 이제 안 좋아해" (false) 리턴
        } else {
            Like like = new Like();
            like.setMember(member);
            like.setPost(post);
            likeRepository.save(like); // 추가
            return true; // "나 이거 좋아해" (true) 리턴
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long memberId, Long postId) {
        return memberRepository.findById(memberId).flatMap(m ->
                postRepository.findById(postId).flatMap(p ->
                        likeRepository.findByMemberAndPost(m, p)
                )).isPresent();
    }
}