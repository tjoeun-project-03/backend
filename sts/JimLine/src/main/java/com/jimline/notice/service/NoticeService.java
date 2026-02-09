package com.jimline.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jimline.notice.domain.Notice;
import com.jimline.notice.dto.NoticeRequest;
import com.jimline.notice.dto.NoticeResponse;
import com.jimline.notice.repository.NoticeRepository;
import com.jimline.user.domain.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;

    // 관리자: 공지 작성
    @Transactional
    public Long createNotice(NoticeRequest dto, User writer) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .target(dto.getTarget())
                .writer(writer)
                .pinned(dto.isPinned())
                .build();
        return noticeRepository.save(notice).getId();
    }

    // 유저: 상세 조회 (조회수 증가는 Controller에서 쿠키 체크 후 호출)
    public NoticeResponse getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        return NoticeResponse.from(notice);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        noticeRepository.findById(id).ifPresent(Notice::incrementViewCount);
    }

    // 관리자: 토글
    @Transactional
    public void togglePinned(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow();
        notice.togglePinned();
    }
    
    // 목록 조회: 고정글(1) -> 일반글(0) 순서, 그 안에서는 최신순
    public List<NoticeResponse> getAllNotices() {
        return noticeRepository.findAllByOrderByPinnedDescCreatedAtDesc()
                .stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }

    // 공지 수정
    @Transactional
    public void updateNotice(Long id, NoticeRequest dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("수정할 공지사항이 없습니다."));
        
        // 엔티티 내부에 만든 update 메서드 활용
        notice.update(dto.getTitle(), dto.getContent(), dto.getTarget(), dto.isPinned());
    }

    // 공지 삭제
    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new EntityNotFoundException("삭제할 공지사항이 없습니다.");
        }
        noticeRepository.deleteById(id);
    }
}