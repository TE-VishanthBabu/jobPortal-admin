package com.job_portal.admin_portal.entity;

import com.job_portal.admin_portal.entity.generic.UUIDEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "experience")
public class ExperienceInfo extends UUIDEntity {
    private String position;
    private String companyName;
    private String from;
    private String to;
    private String jobDescription;
}
