package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.constants.Constant;
import com.job_portal.admin_portal.constants.VerifiedBadge;
import com.job_portal.admin_portal.entity.JobSeeker;
import com.job_portal.admin_portal.entity.JobSeekerVerificationBadge;
import com.job_portal.admin_portal.repository.JobSeekerRepository;
import com.job_portal.admin_portal.request.JobSeekerVerificationBadgeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobSeekerVerificationService {
    private final JobSeekerService jobSeekerService;
    private final JobSeekerRepository jobSeekerRepository;
    private final ModelMapper modelMapper;

    /**
     * Update job-seeker verification badge.
     *
     * @param jobSeekerId
     * @param badgeRequest
     * @return jobSeeker verification badge
     */
    @Transactional
    public JobSeekerVerificationBadge updateVerifiedBadge(String jobSeekerId, JobSeekerVerificationBadgeRequest badgeRequest) {
        JobSeeker seeker = jobSeekerService.getJobSeeker(jobSeekerId);
        JobSeekerVerificationBadge badge = null;
        if(ObjectUtils.isNotEmpty(seeker.getJobSeekerVerificationBadge())) {
            badge = modelMapper.map(badgeRequest, JobSeekerVerificationBadge.class);
            seeker.getJobSeekerVerificationBadge().setIdProofVerification(badge.getIdProofVerification());
            seeker.getJobSeekerVerificationBadge().setEmailVerification(badge.getEmailVerification());
            seeker.getJobSeekerVerificationBadge().setAddressProofVerification(badge.getAddressProofVerification());
            seeker.getJobSeekerVerificationBadge().setVerifiedBadge(badge.getVerifiedBadge());
        } else {
            badge = modelMapper.map(badgeRequest, JobSeekerVerificationBadge.class);
            seeker.setJobSeekerVerificationBadge(badge);
        }
        jobSeekerRepository.save(seeker);
        return seeker.getJobSeekerVerificationBadge();
    }

    /**
     * Get job-seeker verified batch details.
     *
     * @param jobSeekerId
     * @return jobSeeker verification badge
     */
    public JobSeekerVerificationBadge getVerifiedBatchDetails(String jobSeekerId) {
        JobSeeker seeker = jobSeekerService.getJobSeeker(jobSeekerId);
        if((seeker.getJobSeekerVerificationBadge().getEmailVerification().equals(Constant.VERIFIED) &&
                seeker.getJobSeekerVerificationBadge().getIdProofVerification().equals(Constant.VERIFIED)) &&
                seeker.getJobSeekerVerificationBadge().getAddressProofVerification().equals(Constant.VERIFIED)) {
                seeker.getJobSeekerVerificationBadge().setVerifiedBadge(VerifiedBadge.VERIFIED.name());
                seeker.setVerifiedBadge(VerifiedBadge.VERIFIED.name());
        }
        jobSeekerRepository.save(seeker);
        return seeker.getJobSeekerVerificationBadge();
    }
}
