package com.growcorehub.dto;

import java.time.LocalDate;
import java.util.List;

import com.growcorehub.entity.FreelancerProfile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
	private String phone;
	private LocalDate dateOfBirth;
	private String address;

	@NotNull
	private List<FreelancerProfile.Skill> skills;

	@NotNull
	private List<FreelancerProfile.Education> education;

	@NotNull
	private List<FreelancerProfile.Document> documents;
}
