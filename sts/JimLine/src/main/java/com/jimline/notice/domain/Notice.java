package com.jimline.notice.domain;

import java.time.LocalDateTime;

import com.jimline.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTICES")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "CLOB", nullable = false)
    private String content;

    // 0: 전체, 1: 차주, 2: 화주
    @Column(nullable = false)
    private Integer target;

    // 작성자 (User 엔티티와 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    // 1. 컨버터 설정 (DB의 1/0 <-> 자바의 true/false)
    @Column(name = "is_pinned", nullable = false, columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private boolean pinned = false;

    // 조회수
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notice(String title, String content, Integer target, User writer, boolean pinned) {
        this.title = title;
        this.content = content;
        this.target = target;
        this.writer = writer;
        this.pinned = pinned;
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    // --- 비즈니스 로직 ---

    // 수정 기능
    public void update(String title, String content, Integer target, boolean pinned) {
        this.title = title;
        this.content = content;
        this.target = target;
        this.pinned = pinned;
    }

    // 고정/해제 토글
    public void togglePinned() {
        this.pinned = !this.pinned; // true -> false, false -> true
    }

    // 조회수 증가
    public void incrementViewCount() {
        this.viewCount++;
    }
}