package com.growcorehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "sow", columnDefinition = "TEXT")
	private String statementOfWork;

	@Enumerated(EnumType.STRING)
	@Column(name = "project_type")
	private ProjectType projectType;

	@NotNull
	@Column(name = "min_score", nullable = false)
	private Integer minScore;

	@NotNull
	@Column(name = "payout_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal payoutAmount;

	@NotNull
	@Column(name = "billing_cycle_days", nullable = false)
	private Integer billingCycleDays;

	@Column(name = "duration_days")
	private Integer durationDays;

	@Column(name = "crm_provided")
	private Boolean crmProvided = false;

	@Column(name = "crm_url")
	private String crmUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectStatus status = ProjectStatus.ACTIVE;

	@Column(name = "created_by")
	private Long createdBy;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public enum ProjectType {
		DATA_ENTRY, CONTENT_WRITING, VIRTUAL_ASSISTANT, CUSTOMER_SUPPORT, TRANSCRIPTION, TRANSLATION, PROGRAMMING,
		DESIGN, OTHER
	}

	public enum ProjectStatus {
		DRAFT, ACTIVE, PAUSED, CLOSED, CANCELLED
	}
}