package com.growcorehub.repository;

import com.growcorehub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

	List<Application> findByUserId(Long userId);

	List<Application> findByProjectId(Long projectId);

	Optional<Application> findByUserIdAndProjectId(Long userId, Long projectId);

	List<Application> findByStatus(Application.ApplicationStatus status);

	@Query("SELECT a FROM Application a WHERE a.userId = :userId AND a.status = :status")
	List<Application> findByUserIdAndStatus(@Param("userId") Long userId,
			@Param("status") Application.ApplicationStatus status);

	@Query("SELECT a FROM Application a JOIN a.project p WHERE p.createdBy = :adminId")
	List<Application> findApplicationsForAdmin(@Param("adminId") Long adminId);

	boolean existsByUserIdAndProjectId(Long userId, Long projectId);
}