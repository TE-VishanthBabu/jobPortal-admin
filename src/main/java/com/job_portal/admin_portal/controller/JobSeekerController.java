package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.entity.JobSeeker;
import com.job_portal.admin_portal.request.JobSeekerProfileRequest;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class JobSeekerController {
    private final JobSeekerService jobSeekerService;
    private final MessageSource messageSource;


    /**
     * Get all job-seekers.
     *
     * @return list of job-seekers
     */
    @GetMapping("/job-seekers")
    public ResponseEntity<JobPortalResponse> getAllJobSeeker(){
        List<JobSeeker> jobSeekers = this.jobSeekerService.getAllJobSeeker();
        log.info("List of job-Seekers");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobSeekers);
        response.setMessage(messageSource.getMessage("jobSeeker.list",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get jobSeeker by id.
     *
     * @param jobSeekerId
     * @return jobSeeker details
     */
    @GetMapping("/job-seekers/{jobSeekerId}")
    public ResponseEntity<JobPortalResponse> getJobSeeker(@PathVariable String jobSeekerId) {
        JobSeeker jobSeekerDetails = this.jobSeekerService.getJobSeeker(jobSeekerId);
        log.info("Gathered job-seeker details for the id: {}",jobSeekerId);
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobSeekerDetails);
        response.setMessage(messageSource.getMessage("get.jobSeeker",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Delete job-seeker.
     *
     * @param jobSeekerId
     * @return deleted jobSeeker response
     */
    @DeleteMapping("/job-seekers/{jobSeekerId}")
    public ResponseEntity<JobPortalResponse> deleteJobSeeker(@PathVariable String jobSeekerId) {
        this.jobSeekerService.deleteJobSeeker(jobSeekerId);
        JobPortalResponse response = new JobPortalResponse();
        response.setMessage(messageSource.getMessage("delete.jobSeeker",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/job-seekers/{jobSeekerId}")
    public ResponseEntity<JobPortalResponse> updateJobSeekerProfile(@PathVariable String jobSeekerId,@RequestBody JobSeekerProfileRequest profileRequest) {
        JobSeeker jobSeeker = this.jobSeekerService.updateJobSeekerProfile(jobSeekerId,profileRequest);
        log.info("Job-seeker profile has been updated: {}",jobSeeker.getId());
        JobPortalResponse response = new JobPortalResponse();
        response.setData(jobSeeker);
        response.setMessage(messageSource.getMessage("jobSeeker.update",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
