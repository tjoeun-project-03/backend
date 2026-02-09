package com.jimline.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.user.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
    // 아이디로 사용자 찾기
    Optional<User> findByUserId(String userId);
}