package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.dto.DashboardStatDTO;
import com.example.requisitionmanagementapi.service.DashboardStatService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/dashboard")
public class DashboardStatController {

    private final DashboardStatService service;

    @GetMapping
    public DashboardStatDTO get() {
        return service.getDashboardStatDTO();
    }
}
