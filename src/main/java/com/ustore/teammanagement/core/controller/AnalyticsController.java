package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.service.AnalyticsService;
import com.ustore.teammanagement.payload.dto.response.AnalyticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "https://mentoria-nu.vercel.app")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }

    @GetMapping("/trend")
    public ResponseEntity<?> getTaskTrend(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(analyticsService.getTrendData(days));
    }
}
