package com.jimline.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.user.domain.Carrier;

public interface AdminRepository extends JpaRepository<Carrier, String> {
}