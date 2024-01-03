package com.job_portal.admin_portal.service;

import com.job_portal.admin_portal.customException.CommonException;
import com.job_portal.admin_portal.entity.User;
import com.job_portal.admin_portal.repository.UserRepository;
import com.job_portal.admin_portal.request.UserRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public User updateProfile(UserRequest request) {
        User user = null;
        if(request.getId()!=null) {
            user = userRepository.findById(request.getId()).orElseThrow(()->{
                throw new CommonException("Admin Id not found");
            });
        }
        if(user!=null) {
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            if(request.getPassword().equals(request.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            } else {
                throw new CommonException("Password Mismatch",HttpStatus.INTERNAL_SERVER_ERROR);
            }
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setProfileImage(request.getProfileImage());

        } else {
            user = new User();
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            if(request.getPassword().equals(request.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            } else {
                throw new CommonException("Password Mismatch",HttpStatus.INTERNAL_SERVER_ERROR);
            }
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setProfileImage(request.getProfileImage());
        }
        return userRepository.save(user);
    }

    public User getProfile(String userId) {
        return this.userRepository.findById(userId).orElseThrow(()->{
            throw new CommonException("Admin Id not found", HttpStatus.NOT_FOUND);
        });
    }
}
