package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.config.DashboardAPI;
import com.ustore.teammanagement.core.service.DashboardService;
import com.ustore.teammanagement.payload.dto.response.ActivityResponse;
import com.ustore.teammanagement.payload.dto.response.DashboardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "https://mentoria-nu.vercel.app")
public class DashboardController implements DashboardAPI {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<ActivityResponse>> getRecentActivities() {
        return ResponseEntity.ok(dashboardService.getRecentActivities());
    }
}
