package com.ems.management.teams;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
public class ManagementTeamController {

    @GetMapping("/api/teams")
    public ResponseEntity<List<Map<String, Object>>> getTeams() {
        List<Map<String, Object>> teams = new ArrayList<>();
        teams.add(Map.of("id", 1, "name", "Engineering", "color", "blue", "icon", "code", "memberCount", 12, "description", "Product development and infrastructure"));
        teams.add(Map.of("id", 2, "name", "Design", "color", "purple", "icon", "palette", "memberCount", 8, "description", "UI/UX and visual design"));
        teams.add(Map.of("id", 3, "name", "Marketing", "color", "green", "icon", "campaign", "memberCount", 6, "description", "Brand and growth marketing"));
        teams.add(Map.of("id", 4, "name", "Sales", "color", "amber", "icon", "trending_up", "memberCount", 10, "description", "Revenue and customer acquisition"));
        teams.add(Map.of("id", 5, "name", "HR", "color", "red", "icon", "people", "memberCount", 5, "description", "People operations and culture"));
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/api/team/analytics")
    public ResponseEntity<Map<String, Object>> getTeamAnalytics() {
        List<Map<String, Object>> topPerformers = new ArrayList<>();
        topPerformers.add(Map.of("name", "Alice Johnson", "initials", "AJ", "bg", "bg-blue-100", "text", "text-blue-600", "title", "Lead Engineer", "score", 98));
        topPerformers.add(Map.of("name", "Bob Smith", "initials", "BS", "bg", "bg-purple-100", "text", "text-purple-600", "title", "Senior Designer", "score", 95));
        topPerformers.add(Map.of("name", "Carol Davis", "initials", "CD", "bg", "bg-emerald-100", "text", "text-emerald-600", "title", "Product Manager", "score", 92));

        List<Map<String, Object>> charts = new ArrayList<>();
        charts.add(Map.of("id", "team-productivity", "title", "Team Productivity"));
        charts.add(Map.of("id", "skill-growth", "title", "Skill Growth"));

        List<Map<String, Object>> kpis = new ArrayList<>();
        kpis.add(Map.of("label", "Team Score", "value", 92, "extraClass", "text-emerald-600"));
        kpis.add(Map.of("label", "Tasks Completed", "value", 156, "extraClass", "text-blue-600"));
        kpis.add(Map.of("label", "Avg Rating", "value", 4.8, "extraClass", "text-amber-600"));

        return ResponseEntity.ok(Map.of("topPerformers", topPerformers, "charts", charts, "kpis", kpis));
    }
}
