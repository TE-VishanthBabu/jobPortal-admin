package com.job_portal.admin_portal.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String id;
    private String userName;
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String profileImage;
}
