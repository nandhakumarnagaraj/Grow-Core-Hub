package com.growcorehub.service;

import com.growcorehub.dto.AssessmentDTO;
import com.growcorehub.dto.AssessmentSubmissionRequest;
import com.growcorehub.dto.AnswerDTO;
import com.growcorehub.dto.QuestionDTO;
import com.growcorehub.entity.Assessment;
import com.growcorehub.entity.Project;
import com.growcorehub.repository.AssessmentRepository;
import com.growcorehub.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentService {

	private final AssessmentRepository assessmentRepository;
	private final ProjectRepository projectRepository;

	public AssessmentDTO getAssessment(Long assessmentId, Long userId) {
		Assessment assessment = assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new RuntimeException("Assessment not found"));

		if (!assessment.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to assessment");
		}

		return convertToDTO(assessment);
	}

	public AssessmentDTO startAssessment(Long assessmentId, Long userId) {
		Assessment assessment = assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new RuntimeException("Assessment not found"));

		if (!assessment.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to assessment");
		}

		if (assessment.getStatus() != Assessment.AssessmentStatus.NOT_STARTED) {
			throw new RuntimeException("Assessment already started or completed");
		}

		assessment.setStatus(Assessment.AssessmentStatus.IN_PROGRESS);
		assessment = assessmentRepository.save(assessment);

		log.info("Assessment started: {} for user: {}", assessmentId, userId);

		return convertToDTO(assessment);
	}

	public AssessmentDTO submitAssessment(Long assessmentId, Long userId, AssessmentSubmissionRequest request) {
		Assessment assessment = assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new RuntimeException("Assessment not found"));

		if (!assessment.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to assessment");
		}

		if (assessment.getStatus() != Assessment.AssessmentStatus.IN_PROGRESS) {
			throw new RuntimeException("Assessment is not in progress");
		}

		// Convert AnswerDTOs to Assessment.Answer
		List<Assessment.Answer> answers = request.getAnswers().stream().map(this::convertToAnswer)
				.collect(Collectors.toList());

		assessment.setAnswers(answers);
		assessment.setSubmittedAt(LocalDateTime.now());
		assessment.setStatus(Assessment.AssessmentStatus.SUBMITTED);

		// Auto-grade MCQ assessments
		if (assessment.getType() == Assessment.AssessmentType.MCQ) {
			gradeAssessment(assessment);
		}

		assessment = assessmentRepository.save(assessment);

		log.info("Assessment submitted: {} for user: {}", assessmentId, userId);

		return convertToDTO(assessment);
	}

	public AssessmentDTO getAssessmentResult(Long assessmentId, Long userId) {
		Assessment assessment = assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new RuntimeException("Assessment not found"));

		if (!assessment.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access to assessment");
		}

		if (assessment.getStatus() != Assessment.AssessmentStatus.GRADED) {
			throw new RuntimeException("Assessment not yet graded");
		}

		return convertToDTO(assessment);
	}

	public Assessment createAssessment(Long projectId, Long userId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		Assessment assessment = new Assessment();
		assessment.setProjectId(projectId);
		assessment.setUserId(userId);
		assessment.setType(Assessment.AssessmentType.MCQ); // Default to MCQ
		assessment.setStatus(Assessment.AssessmentStatus.NOT_STARTED);

		// Generate sample questions based on project type
		List<Assessment.Question> questions = generateQuestionsForProject(project);
		assessment.setQuestions(questions);

		assessment = assessmentRepository.save(assessment);

		log.info("Assessment created: {} for project: {} and user: {}", assessment.getId(), projectId, userId);

		return assessment;
	}

	private List<Assessment.Question> generateQuestionsForProject(Project project) {
		// Generate sample MCQ questions based on project type
		// In a real implementation, this would be more sophisticated

		Assessment.Question question1 = new Assessment.Question();
		question1.setId("q1");
		question1.setQuestion("What is the primary focus of this project type?");
		question1.setOptions(Arrays.asList("Data Entry", "Content Writing", "Programming", "Design"));
		question1.setCorrectAnswer("Data Entry");
		question1.setPoints(10);

		Assessment.Question question2 = new Assessment.Question();
		question2.setId("q2");
		question2.setQuestion("Which skill is most important for freelance work?");
		question2.setOptions(Arrays.asList("Time Management", "Technical Skills", "Communication", "All of the above"));
		question2.setCorrectAnswer("All of the above");
		question2.setPoints(10);

		Assessment.Question question3 = new Assessment.Question();
		question3.setId("q3");
		question3.setQuestion("How do you ensure quality in your work?");
		question3.setOptions(
				Arrays.asList("Rush through tasks", "Double-check work", "Submit first draft", "Skip reviews"));
		question3.setCorrectAnswer("Double-check work");
		question3.setPoints(10);

		return Arrays.asList(question1, question2, question3);
	}

	private void gradeAssessment(Assessment assessment) {
		int totalScore = 0;
		int maxScore = 0;

		for (Assessment.Question question : assessment.getQuestions()) {
			maxScore += question.getPoints();

			// Find the corresponding answer
			Assessment.Answer answer = assessment.getAnswers().stream()
					.filter(a -> a.getQuestionId().equals(question.getId())).findFirst().orElse(null);

			if (answer != null && answer.getAnswer().equals(question.getCorrectAnswer())) {
				answer.setIsCorrect(true);
				totalScore += question.getPoints();
			} else if (answer != null) {
				answer.setIsCorrect(false);
			}
		}

		// Calculate percentage score
		int percentageScore = maxScore > 0 ? (totalScore * 100) / maxScore : 0;
		assessment.setScore(percentageScore);
		assessment.setStatus(Assessment.AssessmentStatus.GRADED);
		assessment.setGradedBy(Assessment.GradedBy.SYSTEM);

		log.info("Assessment graded: {} - Score: {}/{}", assessment.getId(), percentageScore, 100);
	}

	private Assessment.Answer convertToAnswer(AnswerDTO answerDTO) {
		Assessment.Answer answer = new Assessment.Answer();
		answer.setQuestionId(answerDTO.getQuestionId());
		answer.setAnswer(answerDTO.getAnswer());
		answer.setTextContent(answerDTO.getTextContent());
		return answer;
	}

	private AssessmentDTO convertToDTO(Assessment assessment) {
		AssessmentDTO dto = new AssessmentDTO();
		dto.setId(assessment.getId());
		dto.setProjectId(assessment.getProjectId());
		dto.setType(assessment.getType());
		dto.setScore(assessment.getScore());
		dto.setStatus(assessment.getStatus());
		dto.setCreatedAt(assessment.getCreatedAt());
		dto.setSubmittedAt(assessment.getSubmittedAt());

		// Convert questions (only show questions without correct answers to users)
		if (assessment.getQuestions() != null) {
			List<QuestionDTO> questionDTOs = assessment.getQuestions().stream().map(this::convertQuestionToDTO)
					.collect(Collectors.toList());
			dto.setQuestions(questionDTOs);
		}

		// Set project title if available
		if (assessment.getProject() != null) {
			dto.setProjectTitle(assessment.getProject().getTitle());
		}

		return dto;
	}

	private QuestionDTO convertQuestionToDTO(Assessment.Question question) {
		QuestionDTO dto = new QuestionDTO();
		dto.setId(question.getId());
		dto.setQuestion(question.getQuestion());
		dto.setOptions(question.getOptions());
		dto.setPoints(question.getPoints());
		// Don't include correct answer in DTO for security
		return dto;
	}
}