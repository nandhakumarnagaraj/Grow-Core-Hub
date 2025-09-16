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

		Project project = projectRepository.findById(projectId).orElseThrow();
	}
}