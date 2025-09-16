package com.growcorehub.repository;

import com.growcorehub.entity.FreelancerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreelancerProfileRepository extends JpaRepository<FreelancerProfile, Long> {

	Optional<FreelancerProfile> findByUserId(Long userId);

	List<FreelancerProfile> findByVerificationStatus(FreelancerProfile.VerificationStatus status);

	List<FreelancerProfile> findByCompleted(Boolean completed);

	@Query("SELECT fp FROM FreelancerProfile fp WHERE fp.verificationStatus = 'PENDING'")
	List<FreelancerProfile> findPendingVerification();

	@Query("SELECT fp FROM FreelancerProfile fp WHERE fp.rating >= :minRating")
	List<FreelancerProfile> findByMinRating(@Param("minRating") Double minRating);
}