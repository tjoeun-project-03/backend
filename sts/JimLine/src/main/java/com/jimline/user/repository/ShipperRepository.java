package com.jimline.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jimline.user.domain.Shipper;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, String> {
}