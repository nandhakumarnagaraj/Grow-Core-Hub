package com.growcorehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payout {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", insertable = false, updatable = false)
	private Project project;

	@NotNull
	@Column(name = "amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "scheduled_date")
	private LocalDateTime scheduledDate;

	@Column(name = "paid_date")
	private LocalDateTime paidDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PayoutStatus status = PayoutStatus.SCHEDULED;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "failure_reason")
	private String failureReason;

	@Column(name = "approved_by")
	private Long approvedBy;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public enum PayoutStatus {
		SCHEDULED, PENDING_APPROVAL, APPROVED, PROCESSING, PAID, FAILED, CANCELLED
	}
}