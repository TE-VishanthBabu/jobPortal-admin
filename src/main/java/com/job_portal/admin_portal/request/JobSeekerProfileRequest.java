package com.job_portal.admin_portal.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.job_portal.admin_portal.entity.EducationInfo;
import com.job_portal.admin_portal.entity.ExperienceInfo;
import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerProfileRequest {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private String photo;
    @ElementCollection
    private List<String> preferredJobLocation;
    private String resume;
    private String idProof;
    private String addressProof;
    private String educationProof;
    private String experienceProof;
    private String status;
    private Boolean active;
    private String totalExperience;
    private String title;
    private String summary;
    private List<EducationInfo> education;
    private List<ExperienceInfo> experience;
    private String linkedIn;
    private String twitter;
    private String website;
}
