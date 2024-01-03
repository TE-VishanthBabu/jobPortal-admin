package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.entity.CompanyProfile;
import com.job_portal.admin_portal.request.CompanyProfileRequest;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CompanyProfileController {

    private final CompanyProfileService profileService;
    private final MessageSource messageSource;

    /**
     * Update company profile.
     *
     * @param recruiterId
     * @param profileRequest
     * @return company profile response
     */
    @PostMapping("/recruiters/{recruiterId}/company-profile")
    public ResponseEntity<JobPortalResponse> updateCompanyProfile(@PathVariable String recruiterId, @RequestBody CompanyProfileRequest profileRequest) {
        CompanyProfile profile = this.profileService.updateCompanyProfile(recruiterId,profileRequest);
        log.info("Company profile details added for the id: {}",profile.getId());
        JobPortalResponse response = new JobPortalResponse();
        response.setData(profile);
        response.setMessage(messageSource.getMessage("company.profile",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get company profile details.
     *
     * @param recruiterId
     * @return company profile response
     */
    @GetMapping("/recruiters/{recruiterId}/company-profile")
    public ResponseEntity<JobPortalResponse> getCompanyProfile(@PathVariable String recruiterId){
        CompanyProfile profile = this.profileService.getCompanyProfile(recruiterId);
        log.info("Gathered company profile details for the id: {}",profile.getId());
        JobPortalResponse response = new JobPortalResponse();
        response.setData(profile);
        response.setMessage(messageSource.getMessage("get.profile",null, Locale.getDefault()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
