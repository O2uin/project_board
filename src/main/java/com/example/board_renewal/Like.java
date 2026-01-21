package com.example.board_renewal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Table(name = "likes")
public class Like {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
