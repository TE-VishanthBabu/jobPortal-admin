package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.JobPost;
import com.job_portal.admin_portal.entity.Recruiter;
import com.job_portal.admin_portal.repository.JobApplicationRepository;
import com.job_portal.admin_portal.repository.JobPostRepository;
import com.job_portal.admin_portal.repository.RecruiterRepository;
import com.job_portal.admin_portal.request.JobPostSearchRequest;
import com.job_portal.admin_portal.response.JobPostResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class JobPostService {
    private final RecruiterRepository recruiterRepository;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ModelMapper modelMapper;
    private final ElasticSearchService elasticSearchService;

    /**
     * List of posted jobs.
     *
     * @return get all posted jobs response
     */
    public List<JobPostResponse> getAllJobPostings(String recruiterId){
        Recruiter recruiter = recruiterRepository.findById(recruiterId).orElseThrow(()->{
            throw new CommonException("RecruiterId not found", HttpStatus.NOT_FOUND);
        });
        List<JobPostResponse> jobPostResponses = new ArrayList<>();
            for (JobPost jobPost : recruiter.getJobPost()) {
                if (ObjectUtils.isNotEmpty(jobPost) && jobPost.getPostStatus().equals(true)) {
                    jobPostResponses.add(this.getJobPosts(jobPost));
                }
            }
            return jobPostResponses;
    }

    public List<JobPostResponse> getAllJobPosts(){
        List<Recruiter> recruiters = recruiterRepository.findAll();
        List<JobPostResponse> jobPostResponses = new ArrayList<>();
        for(Recruiter recruiter:recruiters) {
            for (JobPost jobPost : recruiter.getJobPost()) {
                if (ObjectUtils.isNotEmpty(jobPost) && jobPost.getPostStatus().equals(true)) {
                    jobPostResponses.add(this.getJobPosts(jobPost));
                }
            }
        }
        return jobPostResponses;
    }

    public JobPostResponse getJobPosts(JobPost jobPost){
        JobPostResponse jobPostResponse = new JobPostResponse();
        jobPostResponse.setCompanyName(jobPost.getCompanyName());
        jobPostResponse.setPosition(jobPost.getPosition());
        jobPostResponse.setLocation(jobPost.getLocation());
        jobPostResponse.setMinSalary(jobPost.getMinSalary());
        jobPostResponse.setMaxSalary(jobPost.getMaxSalary());
        jobPostResponse.setMinExp(jobPost.getMinExp());
        jobPostResponse.setMaxExp(jobPost.getMaxExp());
        jobPostResponse.setOverview(jobPost.getOverview());
        jobPostResponse.setResponsibilities(jobPost.getResponsibilities());
        jobPostResponse.setKeySkills(jobPost.getKeySkills());
        jobPostResponse.setPostedDate(jobPost.getPostedDate());
        jobPostResponse.setId(jobPost.getId());
        return jobPostResponse;
    }

    /**
     * Get Job post details by id.
     *
     * @param jobPostId
     * @return JobPostResponse
     */
    public JobPostResponse getJobPostById(String jobPostId) {
        JobPost post = jobPostRepository.findById(jobPostId).orElseThrow(()->{
            throw new CommonException("JobPostId not found",HttpStatus.NOT_FOUND);
        });
        JobPostResponse response = modelMapper.map(post,JobPostResponse.class);
        Long countByJobPostId = jobApplicationRepository.countByJobPostId(post.getId());
        response.setJobApplicationCount(countByJobPostId);
        return response;
    }

    public Map<String, Object> searchJobPostsList(JobPostSearchRequest jobPostSearchRequest) {
        return elasticSearchService.searchJobPosts("job_post", jobPostSearchRequest);
    }

}
