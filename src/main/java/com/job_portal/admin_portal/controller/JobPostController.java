package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.request.JobPostSearchRequest;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.response.JobPostResponse;
import com.job_portal.admin_portal.service.JobPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class JobPostController {

    private final JobPostService jobPostService;
    private final MessageSource messageSource;

    /**
     * Get all posted jobs.
     *
     * @return list of jobPosts
     */
    @GetMapping("/{recruiterId}/job-posts")
    public ResponseEntity<JobPortalResponse> getAllJobPostsByRecruiter(@PathVariable String recruiterId){
        List<JobPostResponse> jobPosts = this.jobPostService.getAllJobPostings(recruiterId);
        log.info("Gathered all job-postings");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobPosts);
        response.setMessage(messageSource.getMessage("gathered.jobPosts",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all job postings
     * @return JobPortalResponse
     */
    @GetMapping("/job-posts")
    public ResponseEntity<JobPortalResponse> getAllJobPosting() {
        List<JobPostResponse> jobPosts = this.jobPostService.getAllJobPosts();
        log.info("Gathered all job-postings");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobPosts);
        response.setMessage(messageSource.getMessage("gathered.jobPosts",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get job-post info
     *
     * @param jobPostId
     * @return JobPortalResponse
     */
    @GetMapping("/job-posts/{jobPostId}")
    public ResponseEntity<JobPortalResponse> getJobPosting(@PathVariable String jobPostId) {
        JobPostResponse jobPostResponse = this.jobPostService.getJobPostById(jobPostId);
        log.info("Gathered Job post details");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobPostResponse);
        response.setMessage(messageSource.getMessage("jobPost.details",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Search job-post
     * @param request
     * @return job-post
     */
    @PostMapping("/job-posts/search")
    public ResponseEntity<JobPortalResponse> searchJobPost(@RequestBody JobPostSearchRequest request) {
        Map<String, Object> jobPosts = this.jobPostService.searchJobPostsList(request);
        JobPortalResponse jobPortalResponse = new JobPortalResponse();
        jobPortalResponse.setData(jobPosts);
        return new ResponseEntity<>(jobPortalResponse, HttpStatus.OK);
    }
}
