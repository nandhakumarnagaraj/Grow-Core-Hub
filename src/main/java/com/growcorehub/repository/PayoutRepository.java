package com.growcorehub.repository;

import com.growcorehub.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {

	List<Payout> findByUserId(Long userId);

	List<Payout> findByProjectId(Long projectId);

	List<Payout> findByStatus(Payout.PayoutStatus status);

	@Query("SELECT p FROM Payout p WHERE p.scheduledDate <= :date AND p.status = 'SCHEDULED'")
	List<Payout> findScheduledPayoutsDue(@Param("date") LocalDateTime date);

	@Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payout p WHERE p.userId = :userId AND p.status = 'PAID'")
	BigDecimal getTotalPaidAmountForUser(@Param("userId") Long userId);

	@Query("SELECT p FROM Payout p WHERE p.userId = :userId AND p.status IN :statuses")
	List<Payout> findByUserIdAndStatusIn(@Param("userId") Long userId,
			@Param("statuses") List<Payout.PayoutStatus> statuses);
}