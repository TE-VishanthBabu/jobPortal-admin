package com.job_portal.admin_portal.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.job_portal.admin_portal.entity.Contacts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileRequest {
    private String url;
    private String companyPhoto;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date founded;
    private String employee;
    private Integer location;
    private String industryType;
    private String companyProfileInfo;
    private List<String> techStack;
    private List<String> officeLocation;
    private Contacts contact;
    private String email;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private String idProof;
    private String addressProof;
    private String companyName;
}
