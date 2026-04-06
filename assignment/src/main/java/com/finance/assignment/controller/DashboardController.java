package com.finance.assignment.controller;

import com.finance.assignment.dto.DashboardResponseDTO;
import com.finance.assignment.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardResponseDTO getSummary() {
        return dashboardService.getSummary();
    }
}