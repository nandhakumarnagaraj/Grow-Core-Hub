package com.growcorehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Assessment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", insertable = false, updatable = false)
	private Project project;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "assessment_type", nullable = false)
	private AssessmentType type;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "questions", columnDefinition = "jsonb")
	private List<Question> questions;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "answers", columnDefinition = "jsonb")
	private List<Answer> answers;

	@Column(name = "score")
	private Integer score;

	@Column(name = "duration_seconds")
	private Integer durationSeconds;

	@Column(name = "submitted_at")
	private LocalDateTime submittedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "graded_by")
	private GradedBy gradedBy;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AssessmentStatus status = AssessmentStatus.NOT_STARTED;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// Nested classes for JSON storage
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Question {
		private String id;
		private String question;
		private List<String> options; // for MCQ
		private String correctAnswer; // for auto-grading
		private Integer points;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Answer {
		private String questionId;
		private String answer;
		private String textContent; // for typing tests
		private Integer wordsPerMinute; // for typing tests
		private Double accuracy; // for typing tests
		private Boolean isCorrect;
	}

	public enum AssessmentType {
		MCQ, TYPING, PRACTICAL_UPLOAD, MIXED
	}

	public enum AssessmentStatus {
		NOT_STARTED, IN_PROGRESS, SUBMITTED, GRADED, EXPIRED
	}

	public enum GradedBy {
		SYSTEM, MANUAL
	}
}