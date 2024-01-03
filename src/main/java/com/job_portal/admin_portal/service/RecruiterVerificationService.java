package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.constants.Constant;
import com.job_portal.admin_portal.constants.VerifiedBadge;
import com.job_portal.admin_portal.entity.Recruiter;
import com.job_portal.admin_portal.entity.RecruiterVerificationBadge;
import com.job_portal.admin_portal.repository.RecruiterRepository;
import com.job_portal.admin_portal.request.RecruiterVerificationBadgeRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class RecruiterVerificationService {
    private final RecruiterService recruiterService;
    private final ModelMapper modelMapper;
    private final RecruiterRepository recruiterRepository;

    /**
     * Update recruiter verification badge.
     *
     * @param recruiterId
     * @param badgeRequest
     * @return recruiter verification badge
     */
    @Transactional
    public RecruiterVerificationBadge updateVerifiedBadge(String recruiterId, RecruiterVerificationBadgeRequest badgeRequest) {
        Recruiter recruiter = recruiterService.getRecruiterById(recruiterId);
        RecruiterVerificationBadge badge = null;
        if(ObjectUtils.isNotEmpty(recruiter.getRecruiterVerificationBadge())) {
            badge = modelMapper.map(badgeRequest, RecruiterVerificationBadge.class);
            recruiter.getRecruiterVerificationBadge().setIdProofVerification(badge.getIdProofVerification());
            recruiter.getRecruiterVerificationBadge().setEmailVerification(badge.getEmailVerification());
            recruiter.getRecruiterVerificationBadge().setAddressProofVerification(badge.getAddressProofVerification());
            recruiter.getRecruiterVerificationBadge().setVerifiedBadge(badge.getVerifiedBadge());
        } else {
            badge = modelMapper.map(badgeRequest, RecruiterVerificationBadge.class);
            recruiter.setRecruiterVerificationBadge(badge);
        }
        if(recruiter.getRecruiterVerificationBadge().getVerifiedBadge().equals(VerifiedBadge.VERIFIED.name())) {
            recruiter.setVerifiedBadge(VerifiedBadge.VERIFIED.name());
        } else {
            recruiter.setVerifiedBadge(VerifiedBadge.NOT_VERIFIED.name());
        }
        recruiterRepository.save(recruiter);
        return recruiter.getRecruiterVerificationBadge();
    }

    /**
     * Get recruiter verified batch details.
     *
     * @param recruiterId
     * @return recruiter verification badge
     */
    public RecruiterVerificationBadge getVerifiedBadges(String recruiterId) {
        Recruiter recruiter = recruiterService.getRecruiterById(recruiterId);
        if((recruiter.getRecruiterVerificationBadge().getEmailVerification().equals(Constant.VERIFIED) &&
                recruiter.getRecruiterVerificationBadge().getIdProofVerification().equals(Constant.VERIFIED)) &&
                recruiter.getRecruiterVerificationBadge().getAddressProofVerification().equals(Constant.VERIFIED)) {
            recruiter.getRecruiterVerificationBadge().setVerifiedBadge(VerifiedBadge.VERIFIED.name());

        } else {
            recruiter.getRecruiterVerificationBadge().setVerifiedBadge(VerifiedBadge.NOT_VERIFIED.name());
        }
        recruiterRepository.save(recruiter);
        return recruiter.getRecruiterVerificationBadge();
    }
}
