package com.growcorehub.repository;

import com.growcorehub.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByStatus(Project.ProjectStatus status);

	List<Project> findByProjectType(Project.ProjectType projectType);

	@Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE'")
	List<Project> findActiveProjects();

	@Query("SELECT p FROM Project p WHERE p.createdBy = :userId")
	List<Project> findByCreatedBy(@Param("userId") Long userId);

	@Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE' AND "
			+ "(:projectType IS NULL OR p.projectType = :projectType)")
	List<Project> findActiveProjectsWithFilter(@Param("projectType") Project.ProjectType projectType);
}