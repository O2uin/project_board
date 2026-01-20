package com.example.board_renewal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.persistence.Column;

@Entity // 1. 이게 있어야 DB 테이블이 됩니다.
@Getter @Setter
@NoArgsConstructor // JPA를 쓸 때 꼭 필요한 기본 생성자
public class Post {

    @Id // 2. 이게 PK(주민번호 같은 키)가 됩니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 3. 번호를 자동으로 1, 2, 3... 올려줍니다.
    private Long id;

    private String title;
    private String content;
    private String author; // 작성자
    private Long memberId; // 작성자 id
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int viewCount = 0;//조회수
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likeCount = 0;

    // 작성 시간 추가 (기본값으로 현재 시간 설정)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }
}