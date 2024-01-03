package com.job_portal.admin_portal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse {
    private String experience;
    private String noticePeriod;
    private String JobSeekerId;
    private String location;
    private String resume;
    private String jobSeekerEmail;
}
