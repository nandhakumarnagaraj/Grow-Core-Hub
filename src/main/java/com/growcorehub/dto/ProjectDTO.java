package com.growcorehub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.growcorehub.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
	private Long id;
	private String title;
	private String description;
	private String statementOfWork;
	private Project.ProjectType projectType;
	private Integer minScore;
	private BigDecimal payoutAmount;
	private Integer billingCycleDays;
	private Integer durationDays;
	private Boolean crmProvided;
	private Project.ProjectStatus status;
	private LocalDateTime createdAt;
}
