package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.CompanyProfile;
import com.job_portal.admin_portal.entity.Recruiter;
import com.job_portal.admin_portal.repository.CompanyProfileRepository;
import com.job_portal.admin_portal.repository.RecruiterRepository;
import com.job_portal.admin_portal.response.RecruiterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;
    private final CompanyProfileRepository profileRepository;
    private final MessageSource messageSource;

    /**
     *
     * @return get all recruiters
     */
    public List<RecruiterResponse> getAllRecruiters(){
        List<Recruiter> recruiter = recruiterRepository.findAll();
        List<RecruiterResponse> recruiterResponseList = new ArrayList<>();
        for(Recruiter recruiterInfo:recruiter) {
            RecruiterResponse response = new RecruiterResponse();
            CompanyProfile profile = profileRepository.findByRecruiterId(recruiterInfo.getId());
            response.setRecruiter(recruiterInfo);
            response.setCompanyPhoto(profile.getCompanyPhoto());
            recruiterResponseList.add(response);
        }
        return recruiterResponseList;
    }

    /**
     * Getting recruiter details.
     *
     * @param recruiterId
     * @return recruiter
     */
    public Recruiter getRecruiterById(String recruiterId) {
        return this.recruiterRepository.findById(recruiterId).orElseThrow(()->{
           throw new CommonException(messageSource.getMessage("recruiter.notFound",null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        });
    }

    /**
     * Deleting recruiter.
     *
     * @param recruiterId
     */
    public void deleteRecruiterById(String recruiterId) {
        Recruiter recruiter = this.getRecruiterById(recruiterId);
        recruiter.setDeleted(true);
        recruiterRepository.save(recruiter);
    }
}
