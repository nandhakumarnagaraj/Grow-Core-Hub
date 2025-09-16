package com.growcorehub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growcorehub.dto.ApplicationDTO;
import com.growcorehub.entity.Application;
import com.growcorehub.service.ApplicationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

	private final ApplicationService applicationService;

	@GetMapping
	public ResponseEntity<List<ApplicationDTO>> getUserApplications(@RequestHeader("X-User-ID") Long userId) {
		List<ApplicationDTO> applications = applicationService.getUserApplications(userId);
		return ResponseEntity.ok(applications);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApplicationDTO> getApplication(@PathVariable Long id,
			@RequestHeader("X-User-ID") Long userId) {
		ApplicationDTO application = applicationService.getApplication(id, userId);
		return ResponseEntity.ok(application);
	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<ApplicationDTO>> getProjectApplications(@PathVariable Long projectId) {
		List<ApplicationDTO> applications = applicationService.getProjectApplications(projectId);
		return ResponseEntity.ok(applications);
	}

	@PostMapping("/{id}/sign-agreement")
	public ResponseEntity<ApplicationDTO> signAgreement(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId,
			HttpServletRequest request) {
		String clientIp = getClientIpAddress(request);
		String userAgent = request.getHeader("User-Agent");

		ApplicationDTO application = applicationService.signAgreement(id, userId, clientIp, userAgent);
		return ResponseEntity.ok(application);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<ApplicationDTO> updateApplicationStatus(@PathVariable Long id,
			@RequestParam Application.ApplicationStatus status) {
		ApplicationDTO application = applicationService.updateApplicationStatus(id, status);
		return ResponseEntity.ok(application);
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<ApplicationDTO>> getApplicationsByStatus(
			@PathVariable Application.ApplicationStatus status) {
		List<ApplicationDTO> applications = applicationService.getApplicationsByStatus(status);
		return ResponseEntity.ok(applications);
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
			return xForwardedFor.split(",")[0];
		}

		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
			return xRealIp;
		}

		return request.getRemoteAddr();
	}
}