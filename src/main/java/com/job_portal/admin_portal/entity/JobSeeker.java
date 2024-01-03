package com.job_portal.admin_portal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.job_portal.admin_portal.entity.generic.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Where(clause = "deleted=false")
public class JobSeeker extends AbstractEntity {

    private String userId;
    private String title;
    private String summary;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    @Column(name = "dob")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    @ElementCollection
    private List<String> preferredJobLocation;
    private String photo;
    private String resume;
    private String idProof;
    private String addressProof;
    private String educationProof;
    private String experienceProof;
    private String status;
    private String totalExperience;

    private boolean active = Boolean.TRUE;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<EducationInfo> education;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<ExperienceInfo> experience;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private JobSeekerVerificationBadge jobSeekerVerificationBadge;
    private String linkedIn;
    private String twitter;
    private String website;
    private String verifiedBadge;
}