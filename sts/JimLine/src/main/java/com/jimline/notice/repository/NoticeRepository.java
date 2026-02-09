package com.jimline.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.notice.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 상단 고정글 우선, 그 다음 작성일자 내림차순 정렬
    List<Notice> findAllByOrderByPinnedDescCreatedAtDesc();

    // 특정 타겟(전체+내 역할) 공지만 조회할 때
    List<Notice> findAllByTargetInOrderByPinnedDescCreatedAtDesc(List<Integer> targets);
}