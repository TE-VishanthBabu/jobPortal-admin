package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.repository.JobSeekerRepository;
import com.job_portal.admin_portal.repository.RecruiterRepository;
import com.job_portal.admin_portal.response.DashboardResponse;
import com.job_portal.admin_portal.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {
    private final JobSeekerRepository jobSeekerRepository;
    private final RecruiterRepository recruiterRepository;
    private final DateUtils dateUtil;

    public DashboardResponse getDashboardCount() {
        Integer totalJobSeeker = jobSeekerRepository.findAll().size();
        Integer totalRecruiter = recruiterRepository.findAll().size();
        Long jobSeekerCount = jobSeekerRepository.countByVerifiedBadge("VERIFIED");
        Long recruiterCount = recruiterRepository.countByVerifiedBadge("VERIFIED");
        Date previousDate = dateUtil.convertToDateUsingInstant(dateUtil.convertToLocalDateViaInstant(new Date()).minusDays(15));
        Long newJobSeeker = jobSeekerRepository.countByCreationDateBetween(previousDate, new Date());
        Long newRecruiter = recruiterRepository.countByCreationDateBetween(previousDate, new Date());
        DashboardResponse response = new DashboardResponse();
        response.setTotalJobSeeker(totalJobSeeker);
        response.setTotalRecruiter(totalRecruiter);
        response.setVerifiedJobSeeker(jobSeekerCount);
        response.setVerifiedRecruiter(recruiterCount);
        response.setNewJobSeeker(newJobSeeker);
        response.setNewRecruiter(newRecruiter);
        return response;
    }

}
