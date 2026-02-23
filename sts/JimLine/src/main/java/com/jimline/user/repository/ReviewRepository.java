package com.jimline.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.order.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
