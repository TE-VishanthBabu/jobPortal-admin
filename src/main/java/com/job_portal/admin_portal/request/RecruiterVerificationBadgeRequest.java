package com.job_portal.admin_portal.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterVerificationBadgeRequest {
    private String idProofVerification;
    private String addressProofVerification;
    private String emailVerification;
    private String verifiedBadge;
}
