package com.example.board_renewal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // Member와 Post는 위에서 만든 필드명과 일치해야 함
    Optional<Like> findByMemberAndPost(Member member, Post post);
    long countByPost(Post post);
}