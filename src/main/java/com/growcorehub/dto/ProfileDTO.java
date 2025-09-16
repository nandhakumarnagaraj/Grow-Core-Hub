package com.growcorehub.dto;

import java.time.LocalDate;
import java.util.List;

import com.growcorehub.entity.FreelancerProfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
	private Long userId;
	private String name;
	private String email;
	private String phone;
	private LocalDate dateOfBirth;
	private String address;
	private List<FreelancerProfile.Skill> skills;
	private List<FreelancerProfile.Education> education;
	private List<FreelancerProfile.Document> documents;
	private FreelancerProfile.VerificationStatus verificationStatus;
	private Double rating;
	private Boolean completed;
}
