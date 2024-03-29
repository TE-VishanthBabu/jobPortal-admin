package com.job_portal.admin_portal.entity;

import com.job_portal.admin_portal.entity.generic.UUIDEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PreferredJobLocation extends UUIDEntity {

    private String jobLocation;
}
