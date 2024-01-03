package com.job_portal.admin_portal.entity;

import com.job_portal.admin_portal.entity.generic.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Where(clause = "deleted=false")
public class Recruiter extends AbstractEntity {
    private String companyName;
    private String email;
    @Column(name = "official_id_proof")
    private String idProof;
    private String addressProof;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private Boolean active;
    @OneToMany(cascade = CascadeType.ALL)
    private List<JobPost> jobPost;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private RecruiterVerificationBadge recruiterVerificationBadge;
    private String verifiedBadge;
}
