package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.response.JobApplicationResponse;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.response.MyApplicationResponse;
import com.job_portal.admin_portal.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class JobApplicationController {
    private final JobApplicationService applicationService;
    private final MessageSource messageSource;

    /**
     * To gather all job applications
     *
     * @param jobPostId
     * @return Getting all job applications responses for particular job post
     */
    @GetMapping("/{jobPostId}/job-applications")
    public ResponseEntity<JobPortalResponse> getAllJobApplicationForParticularJobPost(@PathVariable final String jobPostId) {
        List<JobApplicationResponse> jobApplicationResponses = this.applicationService.getAllJobApplicationByJobPost(jobPostId);
        log.info("Gathered all job application for particular job-post id: {}",jobPostId);
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobApplicationResponses);
        response.setMessage(messageSource.getMessage("gathered.jobApplication",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all job-applications.
     *
     * @param jobSeekerId
     * @return list of job-applications
     */
    @GetMapping("/{jobSeekerId}/applications")
    public ResponseEntity<JobPortalResponse> getAllJobApplication(@PathVariable String jobSeekerId) {
        List<MyApplicationResponse> applicationResponses = this.applicationService.getAllApplication(jobSeekerId);
        log.info("Getting all my application for the id : {}",jobSeekerId);
        JobPortalResponse response = new JobPortalResponse();
        response.setData(applicationResponses);
        response.setMessage(messageSource.getMessage("applications",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
