package com.job_portal.admin_portal.entity;

import com.job_portal.admin_portal.entity.generic.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecruiterVerificationBadge extends AbstractEntity {
    private String idProofVerification;
    private String addressProofVerification;
    private String emailVerification;
    private String verifiedBadge;
}
