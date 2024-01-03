package com.job_portal.admin_portal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyApplicationResponse {
    private String companyName;
    private String position;
    private List<String> companyLocation;
    private String appliedDate;
    private String status;
    private String hiringStatus;
    private String experience;
}
