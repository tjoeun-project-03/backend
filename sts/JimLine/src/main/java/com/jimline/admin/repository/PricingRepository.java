package com.jimline.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.admin.domain.Pricing;

public interface PricingRepository extends JpaRepository<Pricing, Long> {

	Optional<Pricing> findFirstByOrderByIdDesc();

}
