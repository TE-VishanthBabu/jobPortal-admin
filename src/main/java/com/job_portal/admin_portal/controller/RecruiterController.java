package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.entity.Recruiter;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.response.RecruiterResponse;
import com.job_portal.admin_portal.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Locale;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final MessageSource messageSource;

    /**
     * Get all recruiters.
     *
     * @return list of recruiters.
     */
    @GetMapping("/recruiters")
    public ResponseEntity<JobPortalResponse> getAllRecruiters(){
        List<RecruiterResponse> allRecruiters = this.recruiterService.getAllRecruiters();
        log.info("List of Recruiters");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(allRecruiters);
        response.setMessage(messageSource.getMessage("recruiter.list",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get details of recruiter by recruiter id.
     *
     * @param recruiterId
     * @return recruiter details
     */
    @GetMapping("/recruiters/{recruiterId}")
    public ResponseEntity<JobPortalResponse> getRecruiter(@PathVariable String recruiterId) {
        Recruiter recruiter = this.recruiterService.getRecruiterById(recruiterId);
        log.info("Gathered recruiter details");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(recruiter);
        response.setMessage(messageSource.getMessage("get.recruiter",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Delete recruiter by recruiter id.
     *
     * @param recruiterId
     * @return recruiter deleted response
     */
    @DeleteMapping("/recruiters/{recruiterId}")
    public ResponseEntity<JobPortalResponse> deleteRecruiter(@PathVariable String recruiterId) {
        this.recruiterService.deleteRecruiterById(recruiterId);
        log.info("Recruiter id: {} deleted",recruiterId);
        JobPortalResponse response = new JobPortalResponse();
        response.setMessage(messageSource.getMessage("delete.recruiter",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
