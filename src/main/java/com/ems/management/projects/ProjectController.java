package com.ems.management.projects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','MANAGEMENT')")
public class ProjectController {
    
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    /**
     * GET /api/projects
     * Returns all projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }
    
    /**
     * GET /api/projects/{id}
     * Returns project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
    
    /**
     * POST /api/projects
     * Creates a new project
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectDTO projectDTO) {
        Project project = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }
    
    /**
     * GET /api/projects/status/{status}
     * Returns projects by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(projectService.getProjectsByStatus(status));
    }
    
    /**
     * GET /api/projects/active
     * Returns all active projects
     */
    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        return ResponseEntity.ok(projectService.getActiveProjects());
    }
    
    /**
     * GET /api/projects/manager/{projectManager}
     * Returns projects managed by a user
     */
    @GetMapping("/manager/{projectManager}")
    public ResponseEntity<List<Project>> getProjectsByProjectManager(@PathVariable String projectManager) {
        return ResponseEntity.ok(projectService.getProjectsByProjectManager(projectManager));
    }
    
    /**
     * GET /api/projects/priority/{priority}
     * Returns projects by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Project>> getProjectsByPriority(@PathVariable String priority) {
        return ResponseEntity.ok(projectService.getProjectsByPriority(priority));
    }
    
    /**
     * GET /api/projects/member/{teamMember}
     * Returns projects for a team member
     */
    @GetMapping("/member/{teamMember}")
    public ResponseEntity<List<Project>> getProjectsByTeamMember(@PathVariable String teamMember) {
        return ResponseEntity.ok(projectService.getProjectsByTeamMember(teamMember));
    }
    
    /**
     * PUT /api/projects/{id}
     * Updates a project
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDTO projectDTO) {
        
        Project project = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(project);
    }
    
    /**
     * PUT /api/projects/{id}/status
     * Updates project status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Project> updateProjectStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String status = request.get("status");
        Project project = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(project);
    }
    
    /**
     * PUT /api/projects/{id}/progress
     * Updates project progress (0-100)
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<Project> updateProjectProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer progress = request.get("progress");
        Project project = projectService.updateProjectProgress(id, progress);
        return ResponseEntity.ok(project);
    }
    
    /**
     * POST /api/projects/{id}/team
     * Adds a team member to the project
     */
    @PostMapping("/{id}/team")
    public ResponseEntity<Map<String, String>> addTeamMember(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String teamMember = request.get("teamMember");
        projectService.addTeamMember(id, teamMember);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Team member added successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/projects/{id}/team/{teamMember}
     * Removes a team member from the project
     */
    @DeleteMapping("/{id}/team/{teamMember}")
    public ResponseEntity<Map<String, String>> removeTeamMember(
            @PathVariable Long id,
            @PathVariable String teamMember) {
        
        projectService.removeTeamMember(id, teamMember);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Team member removed successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/projects/{id}
     * Deletes a project
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Project deleted successfully");
        return ResponseEntity.ok(response);
    }
}
