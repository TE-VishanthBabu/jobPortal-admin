package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.response.DashboardResponse;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class DashboardController {

    private final DashboardService dashboardService;
    private final MessageSource messageSource;

    @GetMapping("/dashboard")
    public ResponseEntity<JobPortalResponse> getDashboardCount() {
        DashboardResponse response = this.dashboardService.getDashboardCount();
        JobPortalResponse jobPortalResponse = new JobPortalResponse();
        jobPortalResponse.setData(response);
        jobPortalResponse.setMessage(messageSource.getMessage("get.dashboardCount",null, Locale.getDefault()));
        return new ResponseEntity<>(jobPortalResponse, HttpStatus.OK);
    }
}
