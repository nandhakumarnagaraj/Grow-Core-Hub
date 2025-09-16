package com.growcorehub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionStartRequest {
	@NotNull
	private Long projectId;

	private String notes;
}
