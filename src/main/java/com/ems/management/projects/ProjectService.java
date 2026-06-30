package com.ems.management.projects;

import com.ems.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(ProjectDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setProjectManager(dto.getProjectManager());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setTargetCompletionDate(dto.getTargetCompletionDate());
        project.setBudget(dto.getBudget());
        project.setPriority(dto.getPriority());
        project.setTeamMembers(dto.getTeamMembers());
        project.setStatus("Planning");
        project.setProgress(0);
        // createdAt and updatedAt set by @PrePersist
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findByStatusOrderByTargetCompletionDateAsc("In Progress");
    }

    public List<Project> getProjectsByProjectManager(String projectManager) {
        return projectRepository.findByProjectManager(projectManager);
    }

    public List<Project> getProjectsByPriority(String priority) {
        return projectRepository.findByPriority(priority);
    }

    public List<Project> getProjectsByTeamMember(String teamMember) {
        return projectRepository.findProjectsByTeamMember(teamMember);
    }

    public Project updateProject(Long id, ProjectDTO dto) {
        Project project = getProjectById(id);

        if (dto.getName() != null) {
            project.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStartDate() != null) {
            project.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            project.setEndDate(dto.getEndDate());
        }
        if (dto.getTargetCompletionDate() != null) {
            project.setTargetCompletionDate(dto.getTargetCompletionDate());
        }
        if (dto.getBudget() != null) {
            project.setBudget(dto.getBudget());
        }
        if (dto.getPriority() != null) {
            project.setPriority(dto.getPriority());
        }
        if (dto.getTeamMembers() != null && !dto.getTeamMembers().isEmpty()) {
            project.setTeamMembers(dto.getTeamMembers());
        }

        // updatedAt handled by @PreUpdate
        return projectRepository.save(project);
    }

    public Project updateProjectStatus(Long id, String status) {
        Project project = getProjectById(id);
        project.setStatus(status);
        // updatedAt handled by @PreUpdate
        return projectRepository.save(project);
    }

    public Project updateProjectProgress(Long id, Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        Project project = getProjectById(id);
        project.setProgress(progress);
        // updatedAt handled by @PreUpdate
        return projectRepository.save(project);
    }

    public void addTeamMember(Long id, String teamMember) {
        Project project = getProjectById(id);
        if (!project.getTeamMembers().contains(teamMember)) {
            project.getTeamMembers().add(teamMember);
            projectRepository.save(project);
        }
    }

    public void removeTeamMember(Long id, String teamMember) {
        Project project = getProjectById(id);
        project.getTeamMembers().remove(teamMember);
        projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
