package com.job_portal.admin_portal.controller;

import com.job_portal.admin_portal.entity.User;
import com.job_portal.admin_portal.request.UserRequest;
import com.job_portal.admin_portal.response.JobPortalResponse;
import com.job_portal.admin_portal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminUserService userService;
    private final MessageSource messageSource;

    @PutMapping("")
    public ResponseEntity<JobPortalResponse> updateProfile(@RequestBody UserRequest request) {
       User profile = userService.updateProfile(request);
       JobPortalResponse response = new JobPortalResponse();
       response.setData(profile);
       response.setMessage(messageSource.getMessage("update.profile",null, Locale.getDefault()));
       return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<JobPortalResponse> getProfile(@PathVariable String userId) {
        JobPortalResponse response = new JobPortalResponse();
        response.setData(userService.getProfile(userId));
        response.setMessage(messageSource.getMessage("get.adminProfile",null,Locale.getDefault()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
