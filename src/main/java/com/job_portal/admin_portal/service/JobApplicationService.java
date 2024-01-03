package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.entity.JobApplication;
import com.job_portal.admin_portal.entity.JobPost;
import com.job_portal.admin_portal.repository.JobApplicationRepository;
import com.job_portal.admin_portal.repository.JobPostRepository;
import com.job_portal.admin_portal.response.JobApplicationResponse;
import com.job_portal.admin_portal.response.MyApplicationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;

    /**
     * Get all job application by job post.
     *
     * @param jobPostId
     * @return Get all job application responses
     */
    public List<JobApplicationResponse> getAllJobApplicationByJobPost(String jobPostId) {
        List<JobApplicationResponse> applications = new ArrayList<>();
        List<JobApplication> jobApplication = this.jobApplicationRepository.findAllByJobPostId(jobPostId);
        if(ObjectUtils.isNotEmpty(jobApplication)) {
            for (JobApplication jobApplication1 : jobApplication) {
                applications.add(this.mapJobApplicationResponse(jobApplication1));
            }
        }
        return applications;
    }

    /**
     * Mapping all job applications.
     *
     * @param application
     * @return job application response
     */
    public JobApplicationResponse mapJobApplicationResponse(JobApplication application){
        JobApplicationResponse response = new JobApplicationResponse();
        response.setJobSeekerId(application.getJobSeekerId());
        response.setLocation(application.getLocation());
        response.setJobSeekerEmail(application.getEmail());
        response.setResume(application.getResume());
        response.setExperience(application.getTotalExperience());
        return response;
    }

    /**
     * Getting all job-applications.
     *
     * @param jobSeekerId
     * @return job-application response
     */
    public List<MyApplicationResponse> getAllApplication(String jobSeekerId) {
        List<JobApplication> jobApplications = jobApplicationRepository.findAllByJobSeekerId(jobSeekerId);
        List<MyApplicationResponse> applicationResponses = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(jobApplications)) {
            for (JobApplication jobApplication : jobApplications) {
                MyApplicationResponse applicationResponse = new MyApplicationResponse();
                    JobPost post = this.getJobPost(jobApplication.getJobPostId());
                    if(post!=null) {
                        applicationResponse.setCompanyName(post.getCompanyName());
                        applicationResponse.setCompanyLocation(post.getLocation());
                        applicationResponse.setPosition(post.getPosition());
                        applicationResponse.setAppliedDate(jobApplication.getAppliedDate());
                        applicationResponse.setHiringStatus(jobApplication.getHiringStatus());
                        applicationResponse.setExperience(jobApplication.getTotalExperience());
                        applicationResponses.add(applicationResponse);
                    }
            }
        }
        return applicationResponses;
    }

    public JobPost getJobPost(String jobPostId) {
        return jobPostRepository.findByIdAndPostStatusTrueAndDeletedFalse(jobPostId);
    }
}
