package com.jimline.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.domain.user.Carrier;

public interface AdminRepository extends JpaRepository<Carrier, String> {
}