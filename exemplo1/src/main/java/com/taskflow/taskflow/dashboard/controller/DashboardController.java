package com.taskflow.taskflow.dashboard.controller;

import com.taskflow.taskflow.dashboard.dto.DashboardResponse;
import com.taskflow.taskflow.dashboard.service.DashboardService;
import com.taskflow.taskflow.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getDashboardSummary(
            @AuthenticationPrincipal UserPrincipal currentUserPrincipal) {
        DashboardResponse summary = dashboardService.getDashboardSummary(currentUserPrincipal);
        return ResponseEntity.ok(summary);
    }
}
