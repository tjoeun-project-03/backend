package com.jimline.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequest {
    private String title;
    private String content;
    private Integer target;
    private boolean pinned; // Oracle 대응: Controller/Service에서 0, 1로 변환 가능
}