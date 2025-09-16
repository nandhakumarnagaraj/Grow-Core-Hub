package com.growcorehub.controller;

import com.growcorehub.dto.AssessmentDTO;
import com.growcorehub.dto.AssessmentSubmissionRequest;
import com.growcorehub.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssessmentController {

	private final AssessmentService assessmentService;

	@GetMapping("/{id}")
	public ResponseEntity<AssessmentDTO> getAssessment(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
		AssessmentDTO assessment = assessmentService.getAssessment(id, userId);
		return ResponseEntity.ok(assessment);
	}

	@PostMapping("/{id}/start")
	public ResponseEntity<AssessmentDTO> startAssessment(@PathVariable Long id,
			@RequestHeader("X-User-ID") Long userId) {
		AssessmentDTO assessment = assessmentService.startAssessment(id, userId);
		return ResponseEntity.ok(assessment);
	}

	@PostMapping("/{id}/submit")
	public ResponseEntity<AssessmentDTO> submitAssessment(@PathVariable Long id,
			@RequestHeader("X-User-ID") Long userId, @Valid @RequestBody AssessmentSubmissionRequest request) {
		AssessmentDTO assessment = assessmentService.submitAssessment(id, userId, request);
		return ResponseEntity.ok(assessment);
	}

	@GetMapping("/{id}/result")
	public ResponseEntity<AssessmentDTO> getAssessmentResult(@PathVariable Long id,
			@RequestHeader("X-User-ID") Long userId) {
		AssessmentDTO assessment = assessmentService.getAssessmentResult(id, userId);
		return ResponseEntity.ok(assessment);
	}
}