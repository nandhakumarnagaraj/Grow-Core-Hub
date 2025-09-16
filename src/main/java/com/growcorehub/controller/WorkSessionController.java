package com.growcorehub.controller;

import com.growcorehub.dto.WorkSessionDTO;
import com.growcorehub.dto.WorkSessionStartRequest;
import com.growcorehub.dto.WorkSessionStopRequest;
import com.growcorehub.service.WorkSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkSessionController {

	private final WorkSessionService workSessionService;

	@PostMapping("/start")
	public ResponseEntity<WorkSessionDTO> startWorkSession(@RequestHeader("X-User-ID") Long userId,
			@Valid @RequestBody WorkSessionStartRequest request) {
		WorkSessionDTO session = workSessionService.startWorkSession(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(session);
	}

	@PostMapping("/stop")
	public ResponseEntity<WorkSessionDTO> stopWorkSession(@RequestHeader("X-User-ID") Long userId,
			@Valid @RequestBody WorkSessionStopRequest request) {
		WorkSessionDTO session = workSessionService.stopWorkSession(userId, request);
		return ResponseEntity.ok(session);
	}

	@GetMapping("/sessions")
	public ResponseEntity<List<WorkSessionDTO>> getUserWorkSessions(@RequestHeader("X-User-ID") Long userId,
			@RequestParam(required = false) Long projectId) {
		List<WorkSessionDTO> sessions = workSessionService.getUserWorkSessions(userId, projectId);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/active")
	public ResponseEntity<WorkSessionDTO> getActiveSession(@RequestHeader("X-User-ID") Long userId) {
		WorkSessionDTO session = workSessionService.getActiveSession(userId);
		if (session != null) {
			return ResponseEntity.ok(session);
		}
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/today-hours")
	public ResponseEntity<Double> getTodayHours(@RequestHeader("X-User-ID") Long userId) {
		Double hours = workSessionService.getTodayHours(userId);
		return ResponseEntity.ok(hours);
	}
}