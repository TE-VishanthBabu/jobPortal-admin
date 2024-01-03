package com.job_portal.admin_portal.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingScheduleRequest {
    private String recruiterId;
    private String jobSeekerId;
    private MeetingRequest meetingRequest;
    private String inviteEmail;
}
