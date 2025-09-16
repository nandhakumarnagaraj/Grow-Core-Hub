package com.growcorehub.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.growcorehub.entity.Assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Assessment DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDTO {
	private Long id;
	private Long projectId;
	private String projectTitle;
	private Assessment.AssessmentType type;
	private List<QuestionDTO> questions;
	private Integer score;
	private Assessment.AssessmentStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime submittedAt;
}