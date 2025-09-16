package com.growcorehub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growcorehub.dto.ApplicationDTO;
import com.growcorehub.entity.Application;
import com.growcorehub.entity.Assessment;
import com.growcorehub.entity.FreelancerProfile;
import com.growcorehub.entity.Project;
import com.growcorehub.repository.ApplicationRepository;
import com.growcorehub.repository.AssessmentRepository;
import com.growcorehub.repository.FreelancerProfileRepository;
import com.growcorehub.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final ProjectRepository projectRepository;
	private final FreelancerProfileRepository profileRepository;
	private final AssessmentRepository assessmentRepository;
	private final AssessmentService assessmentService;

	public ApplicationDTO applyToProject(Long userId, Long projectId) {
		// Check if user already applied
		if (applicationRepository.existsByUserIdAndProjectId(userId, projectId)) {
			throw new RuntimeException("Already applied to this project");
		}

		// Verify project exists and is active
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		if (project.getStatus() != Project.ProjectStatus.ACTIVE) {
			throw new RuntimeException("Project is not active");
		}

		// Check if user profile is complete
		FreelancerProfile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Freelancer profile not found"));

		if (!Boolean.TRUE.equals(profile.getCompleted())) {
			throw new RuntimeException("Please complete your profile before applying");
		}

		// Create assessment first
		Assessment assessment = assessmentService.createAssessment(projectId, userId);

		// Create application
		Application application = new Application();
		application.setUserId(userId);
		application.setProjectId(projectId);
		application.setAssessmentId(assessment.getId());
		application.setStatus(Application.ApplicationStatus.APPLIED);

		application = applicationRepository.save(application);

		log.info("Application created: {} for user: {} on project: {}", application.getId(), userId, projectId);

		return convertToDTO(application);
	}

	public List<ApplicationDTO> getUserApplications(Long userId) {
		List<Application> applications = applicationRepository.findByUserId(userId);
		return applications.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<ApplicationDTO> getProjectApplications(Long projectId) {
		List<Application> applications = applicationRepository.findByProjectId(projectId);
		return applications.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public ApplicationDTO getApplication(Long applicationId, Long userId) {
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		if (!application.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to application");
		}

		return convertToDTO(application);
	}

	public ApplicationDTO updateApplicationStatus(Long applicationId, Application.ApplicationStatus status) {
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		application.setStatus(status);
		application = applicationRepository.save(application);

		log.info("Application status updated: {} - Status: {}", applicationId, status);

		return convertToDTO(application);
	}

	public ApplicationDTO signAgreement(Long applicationId, Long userId, String clientIp, String userAgent) {
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		if (!application.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to application");
		}

		if (application.getStatus() != Application.ApplicationStatus.ELIGIBLE) {
			throw new RuntimeException("Application is not eligible for agreement signing");
		}

		application.setStatus(Application.ApplicationStatus.AGREEMENT_SIGNED);
		application.setSignedAgreementAt(LocalDateTime.now());
		application.setSignatureIp(clientIp);
		application.setSignatureUserAgent(userAgent);

		application = applicationRepository.save(application);

		log.info("Agreement signed for application: {} by user: {}", applicationId, userId);

		return convertToDTO(application);
	}

	public void checkAndUpdateEligibility(Long assessmentId) {
		Assessment assessment = assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new RuntimeException("Assessment not found"));

		if (assessment.getStatus() != Assessment.AssessmentStatus.GRADED) {
			return; // Assessment not yet graded
		}

		// Find the associated application
		Application application = applicationRepository
				.findByUserIdAndProjectId(assessment.getUserId(), assessment.getProjectId())
				.orElseThrow(() -> new RuntimeException("Application not found"));

		// Check if score meets minimum requirement
		Project project = projectRepository.findById(assessment.getProjectId())
				.orElseThrow(() -> new RuntimeException("Project not found"));

		// Check profile verification status
		FreelancerProfile profile = profileRepository.findByUserId(assessment.getUserId())
				.orElseThrow(() -> new RuntimeException("Freelancer profile not found"));

		Application.ApplicationStatus newStatus;

		if (assessment.getScore() != null && assessment.getScore() >= project.getMinScore()) {
			if (profile.getVerificationStatus() == FreelancerProfile.VerificationStatus.APPROVED) {
				newStatus = Application.ApplicationStatus.ELIGIBLE;
			} else {
				newStatus = Application.ApplicationStatus.PENDING_VERIFICATION;
			}
		} else {
			newStatus = Application.ApplicationStatus.REJECTED;
		}

		application.setStatus(newStatus);
		applicationRepository.save(application);

		log.info("Application eligibility updated: {} - Status: {}", application.getId(), newStatus);
	}

	public List<ApplicationDTO> getApplicationsByStatus(Application.ApplicationStatus status) {
		List<Application> applications = applicationRepository.findByStatus(status);
		return applications.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<ApplicationDTO> getApplicationsForAdmin(Long adminId) {
		List<Application> applications = applicationRepository.findApplicationsForAdmin(adminId);
		return applications.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private ApplicationDTO convertToDTO(Application application) {
		ApplicationDTO dto = new ApplicationDTO();
		dto.setId(application.getId());
		dto.setUserId(application.getUserId());
		dto.setProjectId(application.getProjectId());
		dto.setAssessmentId(application.getAssessmentId());
		dto.setStatus(application.getStatus());
		dto.setSignedAgreementAt(application.getSignedAgreementAt());
		dto.setCreatedAt(application.getCreatedAt());

		// Set project title if available
		if (application.getProject() != null) {
			dto.setProjectTitle(application.getProject().getTitle());
		}

		// Set user name if available
		if (application.getUser() != null) {
			dto.setUserName(application.getUser().getName());
			dto.setUserEmail(application.getUser().getEmail());
		}

		return dto;
	}
}