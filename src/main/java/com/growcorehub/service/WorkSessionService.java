package com.growcorehub.service;

import com.growcorehub.dto.WorkSessionDTO;
import com.growcorehub.dto.WorkSessionStartRequest;
import com.growcorehub.dto.WorkSessionStopRequest;
import com.growcorehub.entity.Project;
import com.growcorehub.entity.WorkSession;
import com.growcorehub.repository.ProjectRepository;
import com.growcorehub.repository.WorkSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkSessionService {

	private final WorkSessionRepository workSessionRepository;
	private final ProjectRepository projectRepository;

	private static final double MAX_DAILY_HOURS = 8.0;

	public WorkSessionDTO startWorkSession(Long userId, WorkSessionStartRequest request) {
		// Check if user already has an active session
		workSessionRepository.findActiveSessionByUserId(userId).ifPresent(activeSession -> {
			throw new RuntimeException("You already have an active work session");
		});

		// Check daily hours limit
		Double todayHours = getTodayHours(userId);
		if (todayHours >= MAX_DAILY_HOURS) {
			throw new RuntimeException("Daily work hour limit of " + MAX_DAILY_HOURS + " hours exceeded");
		}

		// Verify project exists
		Project project = projectRepository.findById(request.getProjectId())
				.orElseThrow(() -> new RuntimeException("Project not found"));

		WorkSession session = new WorkSession();
		session.setUserId(userId);
		session.setProjectId(request.getProjectId());
		session.setStartTime(LocalDateTime.now());
		session.setNotes(request.getNotes());
		session.setStatus(WorkSession.SessionStatus.ACTIVE);

		session = workSessionRepository.save(session);

		log.info("Work session started: {} for user: {} on project: {}", session.getId(), userId,
				request.getProjectId());

		return convertToDTO(session);
	}

	public WorkSessionDTO stopWorkSession(Long userId, WorkSessionStopRequest request) {
		WorkSession session = workSessionRepository.findById(request.getSessionId())
				.orElseThrow(() -> new RuntimeException("Work session not found"));

		if (!session.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to work session");
		}

		if (session.getStatus() != WorkSession.SessionStatus.ACTIVE) {
			throw new RuntimeException("Work session is not active");
		}

		LocalDateTime endTime = LocalDateTime.now();
		Duration duration = Duration.between(session.getStartTime(), endTime);
		double hours = duration.toMinutes() / 60.0;

		// Check if stopping this session would exceed daily limit
		Double todayHours = getTodayHours(userId);
		if (todayHours - (session.getHours() != null ? session.getHours() : 0) + hours > MAX_DAILY_HOURS) {
			throw new RuntimeException(
					"Stopping this session would exceed daily work hour limit of " + MAX_DAILY_HOURS + " hours");
		}

		session.setEndTime(endTime);
		session.setHours(Math.round(hours * 100.0) / 100.0); // Round to 2 decimal places
		session.setStatus(WorkSession.SessionStatus.COMPLETED);

		if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
			session.setNotes(session.getNotes() + "\n" + request.getNotes());
		}

		session = workSessionRepository.save(session);

		log.info("Work session stopped: {} - Duration: {} hours", session.getId(), session.getHours());

		return convertToDTO(session);
	}

	public List<WorkSessionDTO> getUserWorkSessions(Long userId, Long projectId) {
		List<WorkSession> sessions;

		if (projectId != null) {
			sessions = workSessionRepository.findByUserIdAndProjectId(userId, projectId);
		} else {
			sessions = workSessionRepository.findByUserId(userId);
		}

		return sessions.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public WorkSessionDTO getActiveSession(Long userId) {
		return workSessionRepository.findActiveSessionByUserId(userId).map(this::convertToDTO).orElse(null);
	}

	public Double getTodayHours(Long userId) {
		LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		return workSessionRepository.getTotalHoursForUserOnDate(userId, today);
	}

	public List<WorkSessionDTO> getSessionsInDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
		List<WorkSession> sessions = workSessionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
		return sessions.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public Double getTotalHoursForProject(Long userId, Long projectId) {
		List<WorkSession> sessions = workSessionRepository.findByUserIdAndProjectId(userId, projectId);
		return sessions.stream().filter(session -> session.getHours() != null).mapToDouble(WorkSession::getHours).sum();
	}

	public void cancelActiveSession(Long userId) {
		workSessionRepository.findActiveSessionByUserId(userId).ifPresent(session -> {
			session.setStatus(WorkSession.SessionStatus.CANCELLED);
			session.setEndTime(LocalDateTime.now());
			workSessionRepository.save(session);

			log.info("Work session cancelled: {} for user: {}", session.getId(), userId);
		});
	}

	private WorkSessionDTO convertToDTO(WorkSession session) {
		WorkSessionDTO dto = new WorkSessionDTO();
		dto.setId(session.getId());
		dto.setProjectId(session.getProjectId());
		dto.setStartTime(session.getStartTime());
		dto.setEndTime(session.getEndTime());
		dto.setHours(session.getHours());
		dto.setNotes(session.getNotes());
		dto.setStatus(session.getStatus());

		// Set project title if available
		if (session.getProject() != null) {
			dto.setProjectTitle(session.getProject().getTitle());
		}

		return dto;
	}
}