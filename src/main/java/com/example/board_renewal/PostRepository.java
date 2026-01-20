package com.example.board_renewal;

import org.springframework.data.jpa.repository.JpaRepository;

// 이 한 줄로 저장, 조회, 삭제 기능이 자동으로 만들어집니다!
public interface PostRepository extends JpaRepository<Post, Long> {
}