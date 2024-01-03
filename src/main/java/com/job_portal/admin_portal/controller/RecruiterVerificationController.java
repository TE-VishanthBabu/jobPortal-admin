package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.MeetingSchedule;
import com.job_portal.admin_portal.entity.Recruiter;
import com.job_portal.admin_portal.entity.RecruiterVerificationBadge;
import com.job_portal.admin_portal.request.MeetingScheduleRequest;
import com.job_portal.admin_portal.request.RecruiterVerificationBadgeRequest;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.response.MeetingScheduleResponse;
import com.job_portal.admin_portal.service.MailService;
import com.job_portal.admin_portal.service.RecruiterService;
import com.job_portal.admin_portal.service.RecruiterVerificationService;
import com.job_portal.admin_portal.service.ZoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Locale;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class RecruiterVerificationController {

    private final ZoomService zoomService;
    private final RecruiterService recruiterService;
    private final MailService mailService;
    private final MessageSource messageSource;
    private final RecruiterVerificationService verificationService;

    @Value("${api.url}")
    private String baseUrl;

    /**
     * Video verification
     *
     * @param recruiterId
     * @param request
     * @return meeting schedule
     */
    @PostMapping("/recruiters/{recruiterId}/video-verification")
    public ResponseEntity<JobPortalResponse> videoVerification(@PathVariable String recruiterId, @RequestBody MeetingScheduleRequest request) {
        Map<String, Object> response = zoomService.createMeeting(request.getMeetingRequest());
        MeetingSchedule schedule = zoomService.assignMeetingForRecruiter(response,recruiterId);
        MeetingScheduleResponse meetingResponse = zoomService.getMeetingDateAndTime(schedule.getStartTime());
        log.info("Meeting has been created :{}",schedule.getMeetingId());
        Recruiter recruiter = this.recruiterService.getRecruiterById(recruiterId);
        try {
            String url = baseUrl+"/meeting?meetingNumber="+schedule.getMeetingId()+"&passCode="+schedule.getPassword()+"&name="+recruiter.getCompanyName();
            String message = "Join below Zoom meeting link in the scheduled date: "+meetingResponse.getInterviewDate()+ " and time: "+meetingResponse.getInterviewTime()+ " for video verification";
            mailService.sendMail(request.getInviteEmail(), "Video Verification", "<br> <strong> Dear " + recruiter.getCompanyName() + ",</strong> <br> "+
                    message+"<br>"+"<a href='" + url +"'>Join Meeting </a>");
        } catch (Exception ex) {
            log.error("Exception :"+ex.getMessage());
            throw new CommonException("Email not send to the recruiter");
        }
        log.info("Email sent successfully");
        JobPortalResponse portalResponse = new JobPortalResponse();
        portalResponse.setMessage(messageSource.getMessage("email.sent",null, Locale.getDefault()));
        portalResponse.setData(schedule);
        return new ResponseEntity<>(portalResponse, HttpStatus.OK);

    }

    /**
     * Update of recruiter verification badge.
     *
     * @param recruiterId
     * @param request
     * @return recruiter verification badge response
     */
    @PutMapping("/recruiters/{recruiterId}/verified-badge")
    public ResponseEntity<JobPortalResponse> verifiedBadge(@PathVariable String recruiterId, @RequestBody(required = false) RecruiterVerificationBadgeRequest request) {
        RecruiterVerificationBadge verifiedBadge = this.verificationService.updateVerifiedBadge(recruiterId,request);
        log.info("Recruiter verified badge updated");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(verifiedBadge);
        response.setMessage(messageSource.getMessage("verified.status",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Get recruiter verification details
     *
     * @param recruiterId
     * @return recruiter verification badge response
     */
    @GetMapping("/recruiters/{recruiterId}/verified-badge")
    public ResponseEntity<JobPortalResponse> getVerifiedBadge(@PathVariable String recruiterId) {
        RecruiterVerificationBadge verifiedBadge = this.verificationService.getVerifiedBadges(recruiterId);
        log.info("Gathered recruiter verified details");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(verifiedBadge);
        response.setMessage(messageSource.getMessage("get.verified.status",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
