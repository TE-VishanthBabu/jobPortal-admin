package com.job_portal.admin_portal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingScheduleResponse {
    private String interviewDate;
    private String interviewTime;
}
