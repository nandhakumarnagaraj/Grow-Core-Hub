package com.growcorehub.repository;

import com.growcorehub.entity.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

	List<WorkSession> findByUserId(Long userId);

	List<WorkSession> findByProjectId(Long projectId);

	List<WorkSession> findByUserIdAndProjectId(Long userId, Long projectId);

	@Query("SELECT ws FROM WorkSession ws WHERE ws.userId = :userId AND ws.status = 'ACTIVE'")
	Optional<WorkSession> findActiveSessionByUserId(@Param("userId") Long userId);

	@Query("SELECT COALESCE(SUM(ws.hours), 0) FROM WorkSession ws "
			+ "WHERE ws.userId = :userId AND DATE(ws.startTime) = DATE(:date)")
	Double getTotalHoursForUserOnDate(@Param("userId") Long userId, @Param("date") LocalDateTime date);

	@Query("SELECT ws FROM WorkSession ws WHERE ws.userId = :userId AND "
			+ "ws.startTime >= :startDate AND ws.startTime <= :endDate")
	List<WorkSession> findByUserIdAndDateRange(@Param("userId") Long userId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}