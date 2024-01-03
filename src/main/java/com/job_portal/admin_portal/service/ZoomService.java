package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.constants.Constant;
import com.job_portal.admin_portal.entity.MeetingSchedule;
import com.job_portal.admin_portal.repository.MeetingScheduleRepository;
import com.job_portal.admin_portal.request.MeetingRequest;
import com.job_portal.admin_portal.response.AuthResponse;
import com.job_portal.admin_portal.response.MeetingScheduleResponse;
import com.job_portal.admin_portal.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoomService {

    @Value("${zoom.baseUrl}")
    private String baseUrl;

    @Value("${zoom.client.accountId}")
    private String accountId;

    @Value("${zoom.client.clientId}")
    private String clientId;

    @Value("${zoom.client.clientSecret}")
    private String clientSecret;

    @Value("${zoom.client.grantType}")
    private String grantType;

    @Value("${zoom.apiUrl}")
    private String apiUrl;

    @Value("${zoom.client.userId}")
    private String userId;

    private final RestTemplate restTemplate;
    private final MeetingScheduleRepository scheduleRepository;
    private final DateUtils dateUtils;

    /**
     * Zoom meeting api call.
     *
     * @param meetingRequest
     * @return Zoom meeting response
     */
    public Map<String, Object> createMeeting(MeetingRequest meetingRequest){
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = this.authenticateZoomClient();
        headers.add(Constants.AUTHORIZATION_HEADER_NAME, authResponse.getTokenType() + " " + authResponse.getAccessToken());
        String url = apiUrl + "/users/{userId}/meetings";
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url).buildAndExpand(params);
        url = builder.toUriString();
        HttpEntity< String > request = new HttpEntity(meetingRequest, headers);
        return this.restTemplate.postForEntity(url, request, Map.class).getBody();
    }

    /**
     * Authenticating zoom client.
     *
     * @return authResponse
     */
    public AuthResponse authenticateZoomClient(){
        HttpHeaders headers = new HttpHeaders();
        String auth = clientId + ":" + clientSecret;
        String base64Creds = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add(Constants.AUTHORIZATION_HEADER_NAME, "Basic " + base64Creds);
        String url = baseUrl + "/oauth/token";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam(Constant.GRANT_TYPE, grantType)
                .queryParam(Constant.ACCOUNT_ID, accountId);
        url = builder.toUriString();
        HttpEntity< String > request = new HttpEntity(headers);
        return this.restTemplate.postForEntity(url, request, AuthResponse.class).getBody();
    }

    /**
     * Storing scheduled meeting details to db.
     *
     * @param meetingResponse
     * @return Scheduled meeting
     */
    public MeetingSchedule assignMeetingForRecruiter(Map<String, Object> meetingResponse,String recruiterId){
        MeetingSchedule schedule = new MeetingSchedule();
        try {
            schedule.setRecruiterId(recruiterId);
            schedule.setMeetingId(meetingResponse.get("id").toString());
            schedule.setPassword(meetingResponse.get("password").toString());
            schedule.setDuration(meetingResponse.get("duration").toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = formatter.format(dateFormat.parse(meetingResponse.get("start_time").toString()));
            Date startTime = formatter.parse(dateTime);
            schedule.setStartTime(startTime);
            schedule.setTopic(meetingResponse.get("topic").toString());
            schedule.setAgenda(meetingResponse.get("agenda").toString());
            schedule.setMeetingResponse(meetingResponse.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return this.scheduleRepository.save(schedule);
    }

    /**
     * Get Formatted meeting date and time.
     *
     * @param startDateTime
     * @return meetingSchedule response
     */
    public MeetingScheduleResponse getMeetingDateAndTime(Date startDateTime) {
        MeetingScheduleResponse meetingSchedule = new MeetingScheduleResponse();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        meetingSchedule.setInterviewDate(dateUtils.formattedDate(dateFormat.format(startDateTime),dateFormat));
        meetingSchedule.setInterviewTime(timeFormat.format(startDateTime));
        return meetingSchedule;

    }

    /**
     * Storing scheduled meeting details to db.
     *
     * @param meetingResponse
     * @return Scheduled meeting
     */
    public MeetingSchedule assignMeetingForJobSeeker(Map<String, Object> meetingResponse,String jobSeekerId){
        MeetingSchedule schedule = new MeetingSchedule();
        try {
            schedule.setJobSeekerId(jobSeekerId);
            schedule.setMeetingId(meetingResponse.get("id").toString());
            schedule.setPassword(meetingResponse.get("password").toString());
            schedule.setDuration(meetingResponse.get("duration").toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = formatter.format(dateFormat.parse(meetingResponse.get("start_time").toString()));
            Date startTime = formatter.parse(dateTime);
            schedule.setStartTime(startTime);
            schedule.setTopic(meetingResponse.get("topic").toString());
            schedule.setAgenda(meetingResponse.get("agenda").toString());
            schedule.setMeetingResponse(meetingResponse.toString());
            schedule.setStatus(true);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return this.scheduleRepository.save(schedule);
    }


}
