package com.job_portal.admin_portal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Integer totalRecruiter;
    private Integer totalJobSeeker;
    private Long verifiedRecruiter;
    private Long verifiedJobSeeker;
    private Long newRecruiter;
    private Long newJobSeeker;
}
