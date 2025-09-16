package com.growcorehub.controller;

import com.growcorehub.dto.ProjectDTO;
import com.growcorehub.dto.ProjectCreateRequest;
import com.growcorehub.entity.Project;
import com.growcorehub.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

	private final ProjectService projectService;

	@GetMapping
	public ResponseEntity<List<ProjectDTO>> getAllProjects(
			@RequestParam(required = false) Project.ProjectType projectType,
			@RequestParam(required = false, defaultValue = "false") Boolean eligibleOnly) {
		List<ProjectDTO> projects = projectService.getAllProjects(projectType, eligibleOnly);
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
		ProjectDTO project = projectService.getProjectById(id);
		return ResponseEntity.ok(project);
	}

	@PostMapping
	public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectCreateRequest request) {
		ProjectDTO project = projectService.createProject(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(project);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id,
			@Valid @RequestBody ProjectCreateRequest request) {
		ProjectDTO project = projectService.updateProject(id, request);
		return ResponseEntity.ok(project);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
		projectService.deleteProject(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/apply")
	public ResponseEntity<Long> applyToProject(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
		Long assessmentId = projectService.applyToProject(id, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(assessmentId);
	}
}