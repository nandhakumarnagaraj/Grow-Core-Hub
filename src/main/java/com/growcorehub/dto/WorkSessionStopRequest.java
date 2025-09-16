package com.growcorehub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionStopRequest {
	@NotNull
	private Long sessionId;

	private String notes;
}