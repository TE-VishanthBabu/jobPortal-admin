package com.job_portal.admin_portal.response;

import com.job_portal.admin_portal.entity.Recruiter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterResponse {
    private Recruiter recruiter;
    private String companyPhoto;
}
