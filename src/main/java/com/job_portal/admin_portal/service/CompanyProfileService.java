package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.CompanyProfile;
import com.job_portal.admin_portal.entity.JobPost;
import com.job_portal.admin_portal.entity.Recruiter;
import com.job_portal.admin_portal.repository.CompanyProfileRepository;
import com.job_portal.admin_portal.repository.JobPostRepository;
import com.job_portal.admin_portal.repository.RecruiterRepository;
import com.job_portal.admin_portal.request.CompanyProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository profileRepository;
    private final RecruiterRepository recruiterRepository;
    private final MessageSource messageSource;
    private final JobPostRepository jobPostRepository;


    /**
     * Get company profile details by recruiter Id.
     *
     * @param recruiterId
     * @return company profile
     */
    public CompanyProfile getCompanyProfile(String recruiterId) {
        return profileRepository.findByRecruiterId(recruiterId);
    }

    /**
     * Update company profile.
     *
     * @param recruiterId
     * @param request
     * @return Updated company profile
     */
    public CompanyProfile updateCompanyProfile(String recruiterId, CompanyProfileRequest request) {
        CompanyProfile profile = profileRepository.findByRecruiterId(recruiterId);
        return this.setCompanyProfile(request,profile,recruiterId);
    }

    /**
     * Method to set the company profile request into company profile entity.
     *
     * @param request
     * @param profile
     * @param recruiterId
     * @return Company  profile entity
     */
    public CompanyProfile setCompanyProfile(CompanyProfileRequest request,CompanyProfile profile,String recruiterId) {
        profile.setCompanyProfileInfo(request.getCompanyProfileInfo());
        profile.setCompanyPhoto(request.getCompanyPhoto());
        profile.setFounded(request.getFounded());
        profile.setEmployee(request.getEmployee());
        profile.setLocation(request.getLocation());
        profile.setUrl(request.getUrl());
        profile.setContact(request.getContact());
        profile.setTechStack(request.getTechStack());
        profile.setIndustryType(request.getIndustryType());
        Recruiter recruiter = recruiterRepository.findById(recruiterId).orElseThrow(()->{
            throw new CommonException(messageSource.getMessage("recruiter.notFound",null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        });
        if(request.getEmail()!=null) {
            recruiter.setEmail(request.getEmail());
            recruiter.setPhoneNumber(request.getPhoneNumber());
            recruiter.setAddressLine1(request.getAddressLine1());
            recruiter.setAddressLine2(request.getAddressLine2());
            recruiter.setCity(request.getCity());
            recruiter.setCountry(request.getCountry());
            recruiter.setPostalCode(request.getPostalCode());
            recruiter.setState(request.getState());
        }
        if (request.getIdProof()!=null && request.getAddressProof()!=null) {
            recruiter.setIdProof(request.getIdProof());
            recruiter.setAddressProof(request.getAddressProof());
        } else {
            recruiter.setIdProof(request.getIdProof());
            recruiter.setAddressProof(request.getAddressProof());
        }
        if(request.getCompanyName()!=null) {
            recruiter.setCompanyName(request.getCompanyName());
        }
        profile.setRecruiter(recruiter);
        List<JobPost> jobPosts = new ArrayList<>();
        for(JobPost post:recruiter.getJobPost()) {
            JobPost jobPost = jobPostRepository.findById(post.getId()).orElseThrow(()->{
                throw new CommonException(messageSource.getMessage("job-post.notFound",null,Locale.getDefault()),HttpStatus.NOT_FOUND);
            });
            jobPosts.add(jobPost);
        }
        profile.setJobPosts(jobPosts);
        profile.setOfficeLocation(request.getOfficeLocation());
        return profileRepository.save(profile);
    }
}
