package com.growcorehub.service;

import com.growcorehub.dto.ProjectCreateRequest;
import com.growcorehub.dto.ProjectDTO;
import com.growcorehub.entity.Application;
import com.growcorehub.entity.Assessment;
import com.growcorehub.entity.Project;
import com.growcorehub.repository.ApplicationRepository;
import com.growcorehub.repository.AssessmentRepository;
import com.growcorehub.repository.ProjectRepository;
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
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ApplicationRepository applicationRepository;
	private final AssessmentRepository assessmentRepository;
	private final AssessmentService assessmentService;

	public List<ProjectDTO> getAllProjects(Project.ProjectType projectType, Boolean eligibleOnly) {
		List<Project> projects;

		if (eligibleOnly) {
			projects = projectRepository.findActiveProjectsWithFilter(projectType);
		} else {
			if (projectType != null) {
				projects = projectRepository.findByProjectType(projectType);
			} else {
				projects = projectRepository.findAll();
			}
		}

		return projects.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public ProjectDTO getProjectById(Long id) {
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
		return convertToDTO(project);
	}

	public ProjectDTO createProject(ProjectCreateRequest request) {
		Project project = new Project();
		project.setTitle(request.getTitle());
		project.setDescription(request.getDescription());
		project.setStatementOfWork(request.getStatementOfWork());
		project.setProjectType(request.getProjectType());
		project.setMinScore(request.getMinScore());
		project.setPayoutAmount(request.getPayoutAmount());
		project.setBillingCycleDays(request.getBillingCycleDays());
		project.setDurationDays(request.getDurationDays());
		project.setCrmProvided(request.getCrmProvided());
		project.setCrmUrl(request.getCrmUrl());
		project.setStatus(Project.ProjectStatus.ACTIVE);
		project.setCreatedBy(1L); // Hardcoded admin ID for now

		project = projectRepository.save(project);

		log.info("Project created: {}", project.getTitle());

		return convertToDTO(project);
	}

	public ProjectDTO updateProject(Long id, ProjectCreateRequest request) {
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));

		project.setTitle(request.getTitle());
		project.setDescription(request.getDescription());
		project.setStatementOfWork(request.getStatementOfWork());
		project.setProjectType(request.getProjectType());
		project.setMinScore(request.getMinScore());
		project.setPayoutAmount(request.getPayoutAmount());
		project.setBillingCycleDays(request.getBillingCycleDays());
		project.setDurationDays(request.getDurationDays());
		project.setCrmProvided(request.getCrmProvided());
		project.setCrmUrl(request.getCrmUrl());

		project = projectRepository.save(project);

		log.info("Project updated: {}", project.getTitle());

		return convertToDTO(project);
	}

	public void deleteProject(Long id) {
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));

		project.setStatus(Project.ProjectStatus.CANCELLED);
		projectRepository.save(project);

		log.info("Project deleted: {}", project.getTitle());
	}

	public Long applyToProject(Long projectId, Long userId) {
		// Check if user already applied
		if (applicationRepository.existsByUserIdAndProjectId(userId, projectId)) {
			throw new RuntimeException("Already applied to this project");
		}

		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		if (project.getStatus() != Project.ProjectStatus.ACTIVE) {
			throw new RuntimeException("Project is not active");
		}

		// Create assessment
		Assessment assessment = assessmentService.createAssessment(projectId, userId);

		// Create application
		Application application = new Application();
		application.setUserId(userId);
		application.setProjectId(projectId);
		application.setAssessmentId(assessment.getId());
		application.setStatus(Application.ApplicationStatus.APPLIED);

		application = applicationRepository.save(application);

		log.info("Application created for project: {} by user: {}", projectId, userId);

		return assessment.getId();
	}

	private ProjectDTO convertToDTO(Project project) {
		ProjectDTO dto = new ProjectDTO();
		dto.setId(project.getId());
		dto.setTitle(project.getTitle());
		dto.setDescription(project.getDescription());
		dto.setStatementOfWork(project.getStatementOfWork());
		dto.setProjectType(project.getProjectType());
		dto.setMinScore(project.getMinScore());
		dto.setPayoutAmount(project.getPayoutAmount());
		dto.setBillingCycleDays(project.getBillingCycleDays());
		dto.setDurationDays(project.getDurationDays());
		dto.setCrmProvided(project.getCrmProvided());
		dto.setStatus(project.getStatus());
		dto.setCreatedAt(project.getCreatedAt());
		return dto;
	}
}