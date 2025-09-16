package com.growcorehub.service;

import com.growcorehub.dto.ProfileDTO;
import com.growcorehub.dto.ProfileUpdateRequest;
import com.growcorehub.entity.FreelancerProfile;
import com.growcorehub.entity.User;
import com.growcorehub.repository.FreelancerProfileRepository;
import com.growcorehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FreelancerProfileService {

	private final FreelancerProfileRepository profileRepository;
	private final UserRepository userRepository;

	public ProfileDTO getProfile(Long userId) {
		FreelancerProfile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Profile not found"));
		return convertToDTO(profile);
	}

	public ProfileDTO createOrUpdateProfile(Long userId, ProfileUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		FreelancerProfile profile = profileRepository.findByUserId(userId).orElse(new FreelancerProfile());

		profile.setUserId(userId);
		profile.setDateOfBirth(request.getDateOfBirth());
		profile.setAddress(request.getAddress());
		profile.setSkills(request.getSkills());
		profile.setEducation(request.getEducation());
		profile.setDocuments(request.getDocuments());

		// Update user phone if provided
		if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
			user.setPhone(request.getPhone());
			userRepository.save(user);
		}

		// Check if profile is complete
		boolean isComplete = isProfileComplete(profile);
		profile.setCompleted(isComplete);

		if (isComplete && profile.getVerificationStatus() == FreelancerProfile.VerificationStatus.PENDING) {
			// In a real app, this would trigger verification process
			log.info("Profile completed for user: {} - ready for verification", userId);
		}

		profile = profileRepository.save(profile);

		log.info("Profile updated for user: {}", userId);

		return convertToDTO(profile);
	}

	public ProfileDTO updateVerificationStatus(Long userId, FreelancerProfile.VerificationStatus status) {
		FreelancerProfile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Profile not found"));

		profile.setVerificationStatus(status);
		profile = profileRepository.save(profile);

		log.info("Verification status updated for user: {} - Status: {}", userId, status);

		return convertToDTO(profile);
	}

	public List<ProfileDTO> getProfilesPendingVerification() {
		List<FreelancerProfile> profiles = profileRepository.findPendingVerification();
		return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<ProfileDTO> getProfilesByVerificationStatus(FreelancerProfile.VerificationStatus status) {
		List<FreelancerProfile> profiles = profileRepository.findByVerificationStatus(status);
		return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public void updateRating(Long userId, Double rating) {
		FreelancerProfile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Profile not found"));

		profile.setRating(rating);
		profileRepository.save(profile);

		log.info("Rating updated for user: {} - Rating: {}", userId, rating);
	}

	private boolean isProfileComplete(FreelancerProfile profile) {
		return profile.getDateOfBirth() != null && profile.getAddress() != null
				&& !profile.getAddress().trim().isEmpty() && profile.getSkills() != null
				&& !profile.getSkills().isEmpty() && profile.getEducation() != null && !profile.getEducation().isEmpty()
				&& profile.getDocuments() != null && !profile.getDocuments().isEmpty();
	}

	private ProfileDTO convertToDTO(FreelancerProfile profile) {
		ProfileDTO dto = new ProfileDTO();
		dto.setUserId(profile.getUserId());
		dto.setDateOfBirth(profile.getDateOfBirth());
		dto.setAddress(profile.getAddress());
		dto.setSkills(profile.getSkills());
		dto.setEducation(profile.getEducation());
		dto.setDocuments(profile.getDocuments());
		dto.setVerificationStatus(profile.getVerificationStatus());
		dto.setRating(profile.getRating());
		dto.setCompleted(profile.getCompleted());

		// Set user details if available
		if (profile.getUser() != null) {
			dto.setName(profile.getUser().getName());
			dto.setEmail(profile.getUser().getEmail());
			dto.setPhone(profile.getUser().getPhone());
		}

		return dto;
	}
}