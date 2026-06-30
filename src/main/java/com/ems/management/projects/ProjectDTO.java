package com.ems.management.projects;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private String projectManager;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime targetCompletionDate;
    private String status;
    private Integer progress;
    private String budget;
    private String priority;
    private List<String> teamMembers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectDTO() {}

    public ProjectDTO(Long id, String name, String description, String projectManager,
                      LocalDateTime startDate, LocalDateTime endDate, LocalDateTime targetCompletionDate,
                      String status, Integer progress, String budget, String priority,
                      List<String> teamMembers, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.name = name; this.description = description;
        this.projectManager = projectManager; this.startDate = startDate; this.endDate = endDate;
        this.targetCompletionDate = targetCompletionDate; this.status = status; this.progress = progress;
        this.budget = budget; this.priority = priority; this.teamMembers = teamMembers;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(LocalDateTime targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public List<String> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<String> teamMembers) { this.teamMembers = teamMembers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
