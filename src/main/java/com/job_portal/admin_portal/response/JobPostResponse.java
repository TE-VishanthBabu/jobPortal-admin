package com.job_portal.admin_portal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostResponse {
    private String id;
    private String companyName;
    private String position;
    private Integer minExp;
    private Integer maxExp;
    private Double minSalary;
    private Double maxSalary;
    private String keySkills;
    private List<String> location;
    private String overview;
    private String responsibilities;
    private Date postedDate;
    private String industryType;
    private String workType;
    private String qualifications;
    private Long jobApplicationCount;
    private Integer openings;
}

