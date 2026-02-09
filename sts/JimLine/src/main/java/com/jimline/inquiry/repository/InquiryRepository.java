package com.jimline.inquiry.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.inquiry.domain.Inquiry;
import com.jimline.user.domain.User;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	List<Inquiry> findByUserOrderByCreatedAtDesc(User user);

}
