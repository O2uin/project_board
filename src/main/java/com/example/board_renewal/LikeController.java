package com.example.board_renewal;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> like(@PathVariable Long postId, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(401).build();

        // 1. 좋아요 토글 실행하고 '현재 상태' 결과 받기 (true/false)
        boolean isLiked = likeService.toggleLike(postId, loginMember.getId());

        // 2. 최신 전체 좋아요 개수 계산
        Post post = postRepository.findById(postId).orElseThrow();
        long updatedCount = likeRepository.countByPost(post);

        // 3. 자바스크립트가 쓸 수 있게 '상태'와 '숫자'를 바구니(Map)에 담기
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("totalLikes", updatedCount);

        return ResponseEntity.ok(result); // 이제 { "isLiked": true, "totalLikes": 5 } 이렇게 나갑니다.
    }
}