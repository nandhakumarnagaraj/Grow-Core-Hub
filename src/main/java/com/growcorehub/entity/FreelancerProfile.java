package com.growcorehub.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "freelancer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerProfile {

	@Id
	@Column(name = "user_id")
	private Long userId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(name = "address", columnDefinition = "TEXT")
	private String address;

	// Store as JSON
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "skills", columnDefinition = "jsonb")
	private List<Skill> skills;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "education", columnDefinition = "jsonb")
	private List<Education> education;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "documents", columnDefinition = "jsonb")
	private List<Document> documents;

	@Enumerated(EnumType.STRING)
	@Column(name = "verification_status")
	private VerificationStatus verificationStatus = VerificationStatus.PENDING;

	@Column(name = "rating")
	private Double rating;

	@Column(name = "completed")
	private Boolean completed = false;

	// Nested classes for JSON storage
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Skill {
		private String name;
		private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
		private Integer yearsOfExperience;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Education {
		private String degree;
		private String institution;
		private String fieldOfStudy;
		private Integer graduationYear;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Document {
		private String type; // ID_PROOF, DEGREE_CERTIFICATE, SKILL_CERTIFICATE
		private String fileName;
		private String fileUrl;
		private String uploadedAt;
	}

	public enum VerificationStatus {
		PENDING, APPROVED, REJECTED, REQUIRES_RESUBMISSION
	}
}