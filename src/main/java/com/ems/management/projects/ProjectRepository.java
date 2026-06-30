package com.ems.management.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByStatus(String status);
    
    List<Project> findByProjectManager(String projectManager);
    
    List<Project> findByPriority(String priority);
    
    @Query("SELECT p FROM Project p WHERE :member MEMBER OF p.teamMembers")
    List<Project> findProjectsByTeamMember(@Param("member") String member);
    
    List<Project> findByStatusOrderByTargetCompletionDateAsc(String status);
}
