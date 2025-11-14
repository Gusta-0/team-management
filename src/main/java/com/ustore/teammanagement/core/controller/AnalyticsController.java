package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.config.AnalyticsAPI;
import com.ustore.teammanagement.core.service.AnalyticsService;
import com.ustore.teammanagement.payload.dto.response.AnalyticsTaskResponse;
import com.ustore.teammanagement.payload.dto.response.MemberPerformanceResponse;
import com.ustore.teammanagement.payload.dto.response.OverviewResponse;
import com.ustore.teammanagement.payload.dto.response.ProjectProgressResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "https://mentoria-nu.vercel.app")
public class AnalyticsController implements AnalyticsAPI {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<OverviewResponse> Overview() {
        return ResponseEntity.ok(analyticsService.AnalyticsOverview());
    }

    @GetMapping("/tasks")
    public ResponseEntity<AnalyticsTaskResponse> getAnalyticsTasks(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(analyticsService.getAnalyticsTasks(days));
    }

    @GetMapping("/members")
    public ResponseEntity<Map<String, Object>> getMembersAnalysis(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable
    ) {
        Page<MemberPerformanceResponse> members =
                analyticsService.getPerformanceByDepartment(department, name, pageable);

        Map<String, Object> response = Map.of(
                "members", members.getContent(),
                "page", members.getNumber(),
                "size", members.getSize(),
                "totalPages", members.getTotalPages(),
                "totalElements", members.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects")
    public ResponseEntity<Map<String, Object>> getProjectsAnalysis(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String name
    ) {
        List<ProjectProgressResponse> projects =
                analyticsService.getProjectProgress(department, name);

        Map<String, Object> response = Map.of(
                "projects", projects
        );
        return ResponseEntity.ok(response);
    }
}
