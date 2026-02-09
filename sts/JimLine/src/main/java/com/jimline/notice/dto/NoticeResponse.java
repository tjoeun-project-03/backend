package com.jimline.notice.dto;

import java.time.LocalDateTime;

import com.jimline.notice.domain.Notice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private Integer target;
    private String writerName;
    private int pinned;
    private int viewCount;
    private LocalDateTime createdAt;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .target(notice.getTarget())
                .writerName(notice.getWriter().getUserName()) // 작성자 이름
                .pinned(notice.isPinned() ? 1 : 0)
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}