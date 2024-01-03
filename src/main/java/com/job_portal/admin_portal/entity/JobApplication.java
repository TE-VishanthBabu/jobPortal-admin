package com.job_portal.admin_portal.entity;

import com.job_portal.admin_portal.entity.generic.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Where(clause = "deleted=false")
public class JobApplication extends AbstractEntity {

    private String resume;
    private String totalExperience;
    private String email;
    private String jobSeekerId;
    private String jobPostId;
    private String location;
    private String appliedDate;
    private String hiringStatus;
}
