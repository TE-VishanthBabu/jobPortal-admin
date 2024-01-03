package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.JobSeeker;
import com.job_portal.admin_portal.entity.JobSeekerVerificationBadge;
import com.job_portal.admin_portal.entity.MeetingSchedule;
import com.job_portal.admin_portal.request.JobSeekerVerificationBadgeRequest;
import com.job_portal.admin_portal.request.MeetingScheduleRequest;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.response.MeetingScheduleResponse;
import com.job_portal.admin_portal.service.JobSeekerService;
import com.job_portal.admin_portal.service.JobSeekerVerificationService;
import com.job_portal.admin_portal.service.MailService;
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
public class JobSeekerVerificationController {

    @Value("${api.url}")
    private String baseUrl;
    private final ZoomService zoomService;
    private final JobSeekerService jobSeekerService;
    private final MailService mailService;
    private final JobSeekerVerificationService jobSeekerVerificationService;
    private final MessageSource messageSource;

    /**
     * Sending email for video verification
     *
     * @param jobSeekerId
     * @param request
     * @return video verification meeting details.
     */
    @PostMapping("/job-seekers/{jobSeekerId}/video-verification")
    public ResponseEntity<JobPortalResponse> videoVerification (@PathVariable String jobSeekerId, @RequestBody MeetingScheduleRequest request) {
        Map<String, Object> response = zoomService.createMeeting(request.getMeetingRequest());
        MeetingSchedule schedule = zoomService.assignMeetingForJobSeeker(response,jobSeekerId);
        MeetingScheduleResponse meetingResponse = zoomService.getMeetingDateAndTime(schedule.getStartTime());
        log.info("Meeting has been created :{}",schedule.getMeetingId());
        JobSeeker seeker = jobSeekerService.getJobSeeker(jobSeekerId);
        String name = seeker.getFirstName()+" "+seeker.getLastName();
        try {
            String url = baseUrl+"/meeting?meetingNumber="+schedule.getMeetingId()+"&passCode="+schedule.getPassword()+"&name="+name;
            String message = "Join below Zoom meeting link in the scheduled date: "+meetingResponse.getInterviewDate()+ " and time: "+meetingResponse.getInterviewTime()+ " for video verification";
            mailService.sendMail(request.getInviteEmail(), "Video Verification", "<br> <strong> Dear " + name + ",</strong> <br> "+
                    message+"<br>"+"<a href='" + url +"'>Join Meeting </a>");
        } catch (Exception ex) {
            log.error("Exception :"+ex.getMessage());
            throw new CommonException("Email not send to the Job-seeker");
        }
        log.info("Email sent successfully");
        JobPortalResponse portalResponse = new JobPortalResponse();
        portalResponse.setMessage(messageSource.getMessage("email.sent",null, Locale.getDefault()));
        portalResponse.setData(schedule);
        return new ResponseEntity<>(portalResponse, HttpStatus.OK);
    }

    /**
     * Update of job-seeker verification badge.
     *
     * @param jobSeekerId
     * @param request    JobSeekerVerificationBadgeRequest
     * @return job-seeker verification badge response
     */
    @PutMapping("/job-seekers/{jobSeekerId}/verified-badge")
    public ResponseEntity<JobPortalResponse> verifiedBadge(@PathVariable String jobSeekerId, @RequestBody JobSeekerVerificationBadgeRequest request) {
        JobSeekerVerificationBadge verifiedBadge = this.jobSeekerVerificationService.updateVerifiedBadge(jobSeekerId,request);
        log.info("Job-seeker verified badge updated");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(verifiedBadge);
        response.setMessage(messageSource.getMessage("verified.status",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Get job-seeker verification details
     *
     * @param jobSeekerId
     * @return job-seeker verification badge response
     */
    @GetMapping("/job-seekers/{jobSeekerId}/verified-badge")
    public ResponseEntity<JobPortalResponse> getVerifiedBatch(@PathVariable String jobSeekerId) {
        JobSeekerVerificationBadge verifiedBadge = this.jobSeekerVerificationService.getVerifiedBatchDetails(jobSeekerId);
        log.info("Gathered job-seeker verified details");
        JobPortalResponse response = new JobPortalResponse();
        response.setData(verifiedBadge);
        response.setMessage(messageSource.getMessage("get.verified.status",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
