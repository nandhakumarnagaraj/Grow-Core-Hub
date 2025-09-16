package com.growcorehub.dto;

import java.math.BigDecimal;

import com.growcorehub.entity.Project;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequest {
	@NotBlank
	private String title;

	private String description;
	private String statementOfWork;
	private Project.ProjectType projectType;

	@NotNull
	@Min(0)
	@Max(100)
	private Integer minScore;

	@NotNull
	@DecimalMin("0.0")
	private BigDecimal payoutAmount;

	@NotNull
	@Min(1)
	private Integer billingCycleDays;

	private Integer durationDays;
	private Boolean crmProvided = false;
	private String crmUrl;
}