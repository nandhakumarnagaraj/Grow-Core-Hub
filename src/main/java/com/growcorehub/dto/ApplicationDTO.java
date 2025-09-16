package com.growcorehub.dto;

import com.growcorehub.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
	private Long id;
	private Long userId;
	private String userName;
	private String userEmail;
	private Long projectId;
	private String projectTitle;
	private Long assessmentId;
	private Application.ApplicationStatus status;
	private LocalDateTime signedAgreementAt;
	private LocalDateTime createdAt;
}