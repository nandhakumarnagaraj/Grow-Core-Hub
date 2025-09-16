package com.growcorehub.repository;

import com.growcorehub.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

	List<Assessment> findByUserId(Long userId);

	List<Assessment> findByProjectId(Long projectId);

	Optional<Assessment> findByUserIdAndProjectId(Long userId, Long projectId);

	List<Assessment> findByStatus(Assessment.AssessmentStatus status);

	@Query("SELECT a FROM Assessment a WHERE a.userId = :userId AND a.status = :status")
	List<Assessment> findByUserIdAndStatus(@Param("userId") Long userId,
			@Param("status") Assessment.AssessmentStatus status);

	@Query("SELECT a FROM Assessment a WHERE a.status = 'SUBMITTED' AND a.gradedBy IS NULL")
	List<Assessment> findPendingManualGrading();
}