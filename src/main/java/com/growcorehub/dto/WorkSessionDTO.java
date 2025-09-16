package com.growcorehub.dto;

import java.time.LocalDateTime;

import com.growcorehub.entity.WorkSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Work Session DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionDTO {
	private Long id;
	private Long projectId;
	private String projectTitle;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Double hours;
	private String notes;
	private WorkSession.SessionStatus status;
}
