package com.example.board_renewal;
import jakarta.persistence.*; // 패키지 임포트 체크!
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity  // "이 클래스로 DB에 테이블을 만들어라!"라는 명령어
@Getter @Setter
@Table(name = "members")
public class Member {

    @Id // 기본키(PK) 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 1, 2, 3... 알아서 올려줌
    private Long id;

    private String loginId;
    private String password;
    private String name;
    private LocalDateTime createdDate;//가입일

    private String role = "USER"; //기본값 일반회원
}