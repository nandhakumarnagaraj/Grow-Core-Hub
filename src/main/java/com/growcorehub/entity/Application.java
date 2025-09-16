package com.growcorehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Application {

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

	@Column(name = "assessment_id")
	private Long assessmentId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assessment_id", insertable = false, updatable = false)
	private Assessment assessment;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ApplicationStatus status = ApplicationStatus.APPLIED;

	@Column(name = "signed_agreement_at")
	private LocalDateTime signedAgreementAt;

	@Column(name = "signature_ip")
	private String signatureIp;

	@Column(name = "signature_user_agent")
	private String signatureUserAgent;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public enum ApplicationStatus {
		APPLIED, ASSESSMENT_IN_PROGRESS, ASSESSMENT_COMPLETED, ELIGIBLE, PENDING_VERIFICATION, AGREEMENT_SIGNED, ACTIVE,
		COMPLETED, REJECTED, CANCELLED
	}
}