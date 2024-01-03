package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.JobSeeker;
import com.job_portal.admin_portal.repository.JobSeekerRepository;
import com.job_portal.admin_portal.request.JobSeekerProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;

    /**
     * Getting list of jobSeeker.
     *
     * @return get all job-seekers
     */
    public List<JobSeeker> getAllJobSeeker(){
        return jobSeekerRepository.findAll();
    }

    /**
     * Getting jobSeeker info.
     *
     * @param jobSeekerId
     * @return jobSeeker
     */
    public JobSeeker getJobSeeker(String jobSeekerId) {
        return jobSeekerRepository.findById(jobSeekerId).orElseThrow(()->{
            throw new CommonException(messageSource.getMessage("jobSeeker.notFound",null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        });
    }

    /**
     * Deleting jobSeeker.
     *
     * @param jobSeekerId
     */
    public void deleteJobSeeker(String jobSeekerId) {
        JobSeeker seeker = this.getJobSeeker(jobSeekerId);
        seeker.setDeleted(true);
        this.jobSeekerRepository.save(seeker);
        log.info("Job-seeker deleted for the id: {}",jobSeekerId);
    }


    /**
     * Update Job-seeker profile.
     *
     * @param request
     * @return Updated job-seeker profile
     */
    public JobSeeker updateJobSeekerProfile(String jobSeekerId,JobSeekerProfileRequest request) {
        log.info("Updating Job-seeker profile!");
        Optional<JobSeeker> newJobSeeker = this.jobSeekerRepository.findById(jobSeekerId);
        if(newJobSeeker.isPresent()) {
           JobSeeker jobSeeker = newJobSeeker.get();
            jobSeeker = this.setJobSeekerProfile(request, jobSeeker);
            return jobSeekerRepository.save(jobSeeker);
        } else {
            throw new CommonException("No job-seeker Id found",HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to set the job-seeker request into job-seeker entity.
     *
     * @param request
     * @param jobSeeker
     * @return Job-seeker profile entity
     */
    public JobSeeker setJobSeekerProfile(JobSeekerProfileRequest request, JobSeeker jobSeeker) {
        jobSeeker.setAddressLine1(request.getAddressLine1());
        jobSeeker.setAddressLine2(request.getAddressLine2());
        jobSeeker.setState(request.getState());
        jobSeeker.setCity(request.getCity());
        jobSeeker.setCountry(request.getCountry());
        jobSeeker.setPostalCode(request.getPostalCode());
        jobSeeker.setSummary(request.getSummary());
        jobSeeker.setExperience(request.getExperience());
        jobSeeker.setEducation(request.getEducation());
        jobSeeker.setIdProof(request.getIdProof());
        jobSeeker.setAddressProof(request.getAddressProof());
        jobSeeker.setEmail(request.getEmail());
        jobSeeker.setPhoneNumber(request.getPhoneNumber());
        jobSeeker.setTwitter(request.getTwitter());
        jobSeeker.setLinkedIn(request.getLinkedIn());
        jobSeeker.setWebsite(request.getWebsite());
        jobSeeker.setPhoto(request.getPhoto());
        return jobSeeker;
    }
}
