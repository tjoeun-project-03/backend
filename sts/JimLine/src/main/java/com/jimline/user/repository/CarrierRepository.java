package com.jimline.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jimline.user.domain.Carrier;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, String> {
	
	long countByAccepted(int accepted);
	
	@Query("select c from Carrier c join fetch c.user where c.accepted = :accepted")
	List<Carrier> findAllByAcceptedWithUser(@Param("accepted") int accepted);
}